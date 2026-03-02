package com.d1ff.chatservice.service.impl;

import com.d1ff.chatservice.dto.request.AddParticipantRequest;
import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.entity.ChatRole;
import com.d1ff.chatservice.exceptions.*;
import com.d1ff.grpc.client.account.AccountGrpcClient;
import com.d1ff.grpc.client.file.FileGrpcClient;
import com.d1ff.chatservice.mapper.response.ChatParticipantResponseMapper;
import com.d1ff.chatservice.mapper.response.ChatResponseMapper;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import com.d1ff.chatservice.service.interfaces.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantResponseMapper chatParticipantResponseMapper;
    private final AccountGrpcClient accountGrpcClient;
    private final ChatResponseMapper chatResponseMapper;
    private final FileGrpcClient fileService;


    @Override
    public ChatParticipantResponse updateUserRole(UpdateParticipantStatusRequest participantStatusRequest, UUID userId) {
        log.info("Updating user role {} by {}", participantStatusRequest.userId(), userId);

        if(participantStatusRequest.newStatus().equals(ChatRole.CREATOR)){
            log.warn("Trying to make user with id:{} is creator",participantStatusRequest.userId());
            throw new AccessDenied("Access denied, user can't make any other chat creator!");
        }

        UsersDto usersDto = getTwoUsers(participantStatusRequest.chatId(), userId, participantStatusRequest.userId());

        if(!usersDto.initiator().getRole().equals(ChatRole.CREATOR)){
            log.warn("User initiator:{} isn't creator", userId);
            throw new AccessDenied("Access denied, user isn't creator");
        }

        if(usersDto.target() == null){
            log.error("Participant {} not found in chat {}", participantStatusRequest.userId(), participantStatusRequest.chatId());
            throw new ParticipantNotFound("Participant or chat isn't exists!");
        }

        if (usersDto.initiator().getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        usersDto.target().setRole(participantStatusRequest.newStatus());
        log.info("User {} role updated to {} in chat {} by {}", participantStatusRequest.userId(),
                participantStatusRequest.newStatus(), participantStatusRequest.chatId(), userId);
        return chatParticipantResponseMapper
                .toChatParticipantResponse(usersDto.target());
    }

    @Override
    public ChatResponse addParticipant(UUID initiatorId, UUID chatId, AddParticipantRequest addParticipantRequest, Pageable pageable) {
        log.info("Adding participant with email {} to chat {} by user {}", addParticipantRequest.email(), chatId, initiatorId);
        UUID userId = accountGrpcClient.getUserIdByEmail(addParticipantRequest.email());

        if (userId == null) {
            log.error("User with email {} not found", addParticipantRequest.email());
            throw new UserNotFound("User with email " + addParticipantRequest.email() + " not found");
        }

        UsersDto usersDto = getTwoUsers(chatId, initiatorId, userId);

        if (usersDto.initiator().getChat().isDeleted() || !usersDto.initiator().getChat().isGroupChat()) {
            throw new ChatNotFound("Chat not found");
        }

        if(usersDto.target() != null){
            log.warn("User {} is already a participant of chat {}", userId, chatId);
            throw new ParticipantAlreadyInChat("User already in chat");
        }

        usersDto.initiator().getChat().addParticipant(userId, ChatRole.PARTICIPANT);
        log.info("User {} added to chat {} as PARTICIPANT", userId, chatId);

        return chatResponseMapper.toChatResponse(usersDto.initiator().getChat(),
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository,
                fileService);
    }

    @Override
    public void kickParticipant(UUID userId, KickParticipantRequest kickParticipantRequest) {
        log.info("Attempting to kick participant {} from chat {} by user {}",
                kickParticipantRequest.participantId(), kickParticipantRequest.chatId(), userId);

        if (userId.equals(kickParticipantRequest.participantId())) {
            log.warn("User {} tried to kick themselves from chat {}", userId, kickParticipantRequest.chatId());
            throw new AccessDenied("You can't kick yourself!");
        }

        UsersDto usersDto = getTwoUsers(kickParticipantRequest.chatId(), userId, kickParticipantRequest.participantId());

        if(usersDto.target() == null){
            log.error("Target participant {} not found in chat {}", kickParticipantRequest.participantId(), kickParticipantRequest.chatId());
            throw new UserNotFound("User not found");
        }

        if (usersDto.initiator().getChat().isDeleted()) {
            throw new ChatNotFound("Chat not found");
        }

        if (usersDto.initiator().getRole().equals(ChatRole.PARTICIPANT)) {
            log.error("User {} with role PARTICIPANT can't kick other users in chat {}", userId, kickParticipantRequest.chatId());
            throw new AccessDenied("Access denied! User can't kick other user!");
        }

        if (usersDto.initiator().getRole().equals(ChatRole.ADMIN) &&
                (usersDto.target().getRole().equals(ChatRole.ADMIN) || usersDto.target().getRole().equals(ChatRole.CREATOR))) {
            log.error("User {} with role ADMIN can't kick user {} with role {} in chat {}",
                    userId, kickParticipantRequest.participantId(), usersDto.target().getRole(), kickParticipantRequest.chatId());
            throw new AccessDenied("Access denied! User does not have the required role");
        }

        chatParticipantRepository.delete(usersDto.target());
        log.info("Participant {} kicked from chat {} by user {}",
                kickParticipantRequest.participantId(), kickParticipantRequest.chatId(), userId);
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
