package com.d1ff.chatservice.service.impl;

import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.entity.ChatRole;
import com.d1ff.chatservice.exceptions.AccessDenied;
import com.d1ff.chatservice.exceptions.ChatNotFound;
import com.d1ff.chatservice.exceptions.ParticipantNotFound;
import com.d1ff.chatservice.exceptions.UserNotFound;
import com.d1ff.chatservice.grpc.AccountGrpcClient;
import com.d1ff.chatservice.mapper.response.ChatParticipantResponseMapper;
import com.d1ff.chatservice.mapper.response.ChatResponseMapper;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import com.d1ff.chatservice.repository.ChatRepository;
import com.d1ff.chatservice.service.interfaces.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantResponseMapper chatParticipantResponseMapper;
    private final AccountGrpcClient accountGrpcClient;
    private final ChatRepository chatRepository;
    private final ChatResponseMapper chatResponseMapper;


    @Override
    public ChatParticipantResponse updateUserRole(UpdateParticipantStatusRequest participantStatusRequest, UUID userId) {
        ChatParticipant creator = chatParticipantRepository.findByUserIdAndChatId(userId, participantStatusRequest.chatId())
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        if(!creator.getRole().equals(ChatRole.CREATOR)){
            throw new AccessDenied("Access denied, user isn't creator");
        }

        ChatParticipant chatParticipant = chatParticipantRepository.findByUserIdAndChatId(participantStatusRequest.userId(),
                        participantStatusRequest.chatId())
                .orElseThrow(() -> new ParticipantNotFound("Participant or chat isn't exists!"));

        if (chatParticipant.getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        chatParticipant.setRole(participantStatusRequest.newStatus());
        return chatParticipantResponseMapper
                .toChatParticipantResponse(chatParticipant);
    }

    @Override
    public ChatResponse addParticipant(UUID chatId, String email, Pageable pageable) {
        UUID userId = accountGrpcClient.getUserIdByEmail(email);

        if (userId == null) {
            throw new UserNotFound("User with email " + email + " not found");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        if (chat.isDeleted() || chat.isGroupChat()) {
            throw new ChatNotFound("Chat not found");
        }

        chat.addParticipant(userId, ChatRole.PARTICIPANT);

        return chatResponseMapper.toChatResponse(chat,
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository);
    }

    @Override
    public void kickParticipant(UUID userId, KickParticipantRequest kickParticipantRequest) {
        if (userId.equals(kickParticipantRequest.participantId())) {
            throw new AccessDenied("You can't kick yourself!");
        }

        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserIdsAndChatId(
                List.of(userId, kickParticipantRequest.participantId()),
                kickParticipantRequest.chatId());

        ChatParticipant initiator = participants.stream()
                .filter(cp -> cp.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        ChatParticipant target = participants.stream()
                .filter(cp -> cp.getUserId().equals(kickParticipantRequest.participantId()))
                .findFirst()
                .orElseThrow(() -> new UserNotFound("User not found"));

        if (initiator.getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        if (initiator.getRole().equals(ChatRole.PARTICIPANT)) {
            throw new AccessDenied("Access denied! User can't kick other user!");
        }

        if (initiator.getRole().equals(ChatRole.ADMIN) &&
                (target.getRole().equals(ChatRole.ADMIN) || target.getRole().equals(ChatRole.CREATOR))) {
            throw new AccessDenied("Access denied! User does not have the required role");
        }

        chatParticipantRepository.delete(target);
    }
}
