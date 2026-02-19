package com.d1ff.chatservice.service.impl;

import com.d1ff.chatservice.dto.request.AddParticipantRequest;
import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.entity.ChatRole;
import com.d1ff.chatservice.exceptions.*;
import com.d1ff.chatservice.grpc.AccountGrpcClient;
import com.d1ff.chatservice.grpc.FileGrpcClient;
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
    private final ChatResponseMapper chatResponseMapper;
    private final FileGrpcClient fileService;


    @Override
    public ChatParticipantResponse updateUserRole(UpdateParticipantStatusRequest participantStatusRequest, UUID userId) {
        if(participantStatusRequest.newStatus().equals(ChatRole.CREATOR)){
            throw new AccessDenied("Access denied, user can't make any other chat creator!");
        }

        UsersDto usersDto = getTwoUsers(participantStatusRequest.chatId(), userId, participantStatusRequest.userId());

        if(!usersDto.initiator().getRole().equals(ChatRole.CREATOR)){
            throw new AccessDenied("Access denied, user isn't creator");
        }

        if(usersDto.target() == null){
            throw new ParticipantNotFound("Participant or chat isn't exists!");
        }

        if (usersDto.initiator().getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        usersDto.target().setRole(participantStatusRequest.newStatus());
        return chatParticipantResponseMapper
                .toChatParticipantResponse(usersDto.target());
    }

    @Override
    public ChatResponse addParticipant(UUID initiatorId, UUID chatId, AddParticipantRequest addParticipantRequest, Pageable pageable) {
        UUID userId = accountGrpcClient.getUserIdByEmail(addParticipantRequest.email());

        if (userId == null) {
            throw new UserNotFound("User with email " + addParticipantRequest.email() + " not found");
        }

        UsersDto usersDto = getTwoUsers(chatId, initiatorId, userId);

        if (usersDto.initiator().getChat().isDeleted() || !usersDto.initiator().getChat().isGroupChat()) {
            throw new ChatNotFound("Chat not found");
        }

        if(usersDto.target() != null){
            throw new ParticipantAlreadyInChat("User already in chat");
        }

        usersDto.initiator().getChat().addParticipant(userId, ChatRole.PARTICIPANT);

        return chatResponseMapper.toChatResponse(usersDto.initiator().getChat(),
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository,
                fileService);
    }

    @Override
    public void kickParticipant(UUID userId, KickParticipantRequest kickParticipantRequest) {
        if (userId.equals(kickParticipantRequest.participantId())) {
            throw new AccessDenied("You can't kick yourself!");
        }

        UsersDto usersDto = getTwoUsers(kickParticipantRequest.chatId(), userId, kickParticipantRequest.participantId());

        if(usersDto.target() == null){
            throw new UserNotFound("User not found");
        }

        if (usersDto.initiator().getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        if (usersDto.initiator().getRole().equals(ChatRole.PARTICIPANT)) {
            throw new AccessDenied("Access denied! User can't kick other user!");
        }

        if (usersDto.initiator().getRole().equals(ChatRole.ADMIN) &&
                (usersDto.target().getRole().equals(ChatRole.ADMIN) || usersDto.target().getRole().equals(ChatRole.CREATOR))) {
            throw new AccessDenied("Access denied! User does not have the required role");
        }

        chatParticipantRepository.delete(usersDto.target());
    }

    private UsersDto getTwoUsers(UUID chatId, UUID initiator, UUID target){
        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserIdsAndChatId(
                List.of(target, initiator),
                chatId);

        ChatParticipant initiatorEntity = participants.stream()
                .filter(cp -> cp.getUserId().equals(initiator))
                .findFirst()
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        ChatParticipant targetEntity = participants.stream()
                .filter(cp -> cp.getUserId().equals(target))
                .findFirst()
                .orElse(null);

        return new UsersDto(initiatorEntity, targetEntity);
    }

    private record UsersDto(ChatParticipant initiator,
                            ChatParticipant target){}
}
