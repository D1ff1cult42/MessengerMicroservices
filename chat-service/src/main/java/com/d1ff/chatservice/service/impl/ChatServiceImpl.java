package com.d1ff.chatservice.service.impl;

import com.d1ff.chatservice.dto.request.CreateChatRequest;
import com.d1ff.chatservice.dto.request.CreateOneToOneChatRequest;
import com.d1ff.chatservice.dto.request.UpdateChatRequest;
import com.d1ff.chatservice.dto.response.AccountGrpcResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.entity.ChatRole;
import com.d1ff.chatservice.exceptions.AccessDenied;
import com.d1ff.chatservice.exceptions.ChatNotFound;
import com.d1ff.chatservice.exceptions.ParticipantNotFound;
import com.d1ff.chatservice.exceptions.UserNotFound;
import com.d1ff.chatservice.grpc.AccountGrpcClient;
import com.d1ff.chatservice.grpc.FileGrpcClient;
import com.d1ff.chatservice.mapper.response.ChatParticipantResponseMapper;
import com.d1ff.chatservice.mapper.response.ChatResponseMapper;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import com.d1ff.chatservice.repository.ChatRepository;
import com.d1ff.chatservice.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.d1ff.bucket.BucketResolver;
import org.d1ff.dto.response.FileUploadResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final AccountGrpcClient accountGrpcClient;
    private final FileGrpcClient fileService;
    private final BucketResolver bucketResolver;
    private final ChatParticipantResponseMapper chatParticipantResponseMapper;
    private final ChatResponseMapper chatResponseMapper;

    @Override
    public ChatResponse createGroupChat(UUID userId, CreateChatRequest createChatRequest, Pageable pageable) {
        log.info("Creating group chat '{}' by user {}", createChatRequest.name(), userId);
        Chat chat = new Chat();

        if(createChatRequest.multipartFile() != null){
            log.info("Uploading icon for group chat '{}'", createChatRequest.name());
            FileUploadResponse fileUploadResponse = fileService.uploadFile(
                    bucketResolver.resolveBucket(createChatRequest
                            .multipartFile()
                            .getOriginalFilename()), createChatRequest
                            .multipartFile());

            chat.setIconBucketName(fileUploadResponse.bucketName());
            chat.setIconObjectName(fileUploadResponse.objectName());
            chat.setIconFileSize(createChatRequest
                    .multipartFile().getSize());
        }

        chat.setName(createChatRequest.name());
        chat.setDescription(createChatRequest.description());
        chat.setGroupChat(true);
        chat.setCreatedBy(userId);

        chat.addParticipant(userId, ChatRole.CREATOR);

        chatRepository.save(chat);

        log.info("Group chat created with ID {} by user {}", chat.getId(), userId);
        return chatResponseMapper.toChatResponse(chat,
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository,
                fileService);
    }

    @Override
    public ChatResponse createOneToOneChat(UUID userId, CreateOneToOneChatRequest createOneToOneChatRequest, Pageable pageable) {
        log.info("Creating one-to-one chat by user {} with {}", userId, createOneToOneChatRequest.otherUserEmail());
        AccountGrpcResponse grpcResponse = accountGrpcClient.getNameAndUserIdAndUserIconByEmail(
                createOneToOneChatRequest.otherUserEmail());
        if (grpcResponse.userId() == null) {
            log.error("User with email {} not found", createOneToOneChatRequest.otherUserEmail());
            throw new UserNotFound("User with email " + createOneToOneChatRequest.otherUserEmail()
                    + " not found");
        }
        Chat chat = Chat.builder()
                .name(grpcResponse.name())
                .iconBucketName(grpcResponse.bucketName())
                .iconObjectName(grpcResponse.iconObjectName())
                .isGroupChat(false)
                .createdBy(userId)
                .build();

        chat.addParticipant(userId, ChatRole.ADMIN);
        chat.addParticipant(grpcResponse.userId(), ChatRole.ADMIN);

        chatRepository.save(chat);

        log.info("One-to-one chat created with ID {} between user {} and user {}", chat.getId(), userId, grpcResponse.userId());
        return chatResponseMapper.toChatResponse(chat,
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository,
                fileService);
    }

    @Override
    public ChatResponse getChat(UUID chatId, Pageable pageable){
       log.info("Getting chat with ID {}", chatId);
       Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));
       if(chat.isDeleted()){
           throw new ChatNotFound("Chat not found");
       }
       return chatResponseMapper.toChatResponse(chat,
               pageable,
               chatParticipantResponseMapper,
               chatParticipantRepository,
               fileService);
    }

    @Override
    public void deleteChat(UUID userId, UUID chatId){
        log.info("Attempting to delete chat {} by user {}", chatId, userId);

        ChatParticipant chatParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId)
                .orElseThrow(() -> new ParticipantNotFound("Participant isn't in chat"));

        if(!chatParticipant.getRole().equals(ChatRole.CREATOR)){
            log.error("User {} is not the creator of chat {}. Deletion denied.", userId, chatId);
            throw new AccessDenied("Not creator can't delete chat!");
        }

        if(chatParticipant.getChat().isDeleted()){
            throw new ChatNotFound("Chat not found");
        }

        chatParticipant.getChat().setDeleted(true);
        log.info("Chat {} marked as deleted by user {}", chatId, userId);
    }

    @Override
    public ChatResponse updateChat(UpdateChatRequest updateChatRequest, UUID chatId, UUID userId, Pageable pageable){
        log.info("Attempting to update chat {} by user {}", chatId, userId);
        ChatParticipant chatParticipant = chatParticipantRepository
                .findByUserIdAndChatId(userId, chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        if(!chatParticipant.getRole().equals(ChatRole.CREATOR)) {
            log.error("User {} is not the creator of chat {}. Update denied.", userId, chatId);
            throw new AccessDenied("Access denied! User isn't CREATOR");
        }

        if(!chatParticipant.getChat().isGroupChat() || chatParticipant.getChat().isDeleted()){
            throw new ChatNotFound("Chat not found");
        }

        if (updateChatRequest.multipartFile() != null) {
            log.info("Uploading new icon for chat {}", chatId);
            FileUploadResponse fileUploadResponse = fileService.uploadFile(
                    bucketResolver.resolveBucket(updateChatRequest
                            .multipartFile()
                            .getOriginalFilename()), updateChatRequest
                            .multipartFile());

            chatParticipant.getChat().setIconBucketName(fileUploadResponse.bucketName());
            chatParticipant.getChat().setIconObjectName(fileUploadResponse.objectName());
            chatParticipant.getChat().setIconFileSize(updateChatRequest
                    .multipartFile().getSize());
        }

        if (updateChatRequest.name() != null) {
            chatParticipant.getChat().setName(updateChatRequest.name());
        }

        if (updateChatRequest.description() != null) {
            chatParticipant.getChat().setDescription(updateChatRequest.description());
        }

        log.info("Chat {} updated by user {}", chatId, userId);
        return chatResponseMapper.toChatResponse(chatParticipant.getChat(),
                pageable,
                chatParticipantResponseMapper,
                chatParticipantRepository,
                fileService);
    }
}
