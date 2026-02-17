package com.d1ff.chatservice.service;

import com.d1ff.chatservice.dto.request.CreateChatRequest;
import com.d1ff.chatservice.dto.request.CreateOneToOneChatRequest;
import com.d1ff.chatservice.dto.request.UpdateChatRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.AccountGrpcResponse;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.entity.ChatRole;
import com.d1ff.chatservice.exceptions.ChatNotFound;
import com.d1ff.chatservice.exceptions.UserNotFound;
import com.d1ff.chatservice.grpc.AccountGrpcClient;
import com.d1ff.chatservice.grpc.FileGrpcClient;
import com.d1ff.chatservice.mapper.response.ChatParticipantResponseMapper;
import com.d1ff.chatservice.mapper.response.ChatResponseMapper;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import com.d1ff.chatservice.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.d1ff.bucket.BucketResolver;
import org.d1ff.dto.response.FileUploadResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final AccountGrpcClient accountGrpcClient;
    private final FileGrpcClient fileService;
    private final BucketResolver bucketResolver;
    private final ChatParticipantResponseMapper chatParticipantResponseMapper;
    private final ChatResponseMapper chatResponseMapper;

    @Transactional
    public ChatResponse createGroupChat(UUID userId, CreateChatRequest createChatRequest) {
        Chat chat = new Chat();

        if(createChatRequest.multipartFile() == null){
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

        ChatParticipant creator = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.CREATOR)
                .build();

        return chatResponseMapper.toChatResponse(chat,
                chatParticipantResponseMapper,
                chatParticipantRepository);
    }

    @Transactional
    public ChatResponse createOneToOneChat(UUID userId, CreateOneToOneChatRequest createOneToOneChatRequest) {
        AccountGrpcResponse grpcResponse = accountGrpcClient.getNameAndUserIdAndUserIconByEmail(
                createOneToOneChatRequest.otherUserEmail());
        if (grpcResponse.userId() == null) {
            throw new UserNotFound("User with email " + createOneToOneChatRequest.otherUserEmail()
                    + " not found");
        }
        Chat chat = Chat.builder()
                .name(grpcResponse.name())
                .iconBucketName(grpcResponse.bucketName())
                .iconObjectName(grpcResponse.iconObjectName())
                .isGroupChat(false)
                .build();

        ChatParticipant participant1 = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.ADMIN)
                .build();

        ChatParticipant participant2 = ChatParticipant.builder()
                .chat(chat)
                .userId(grpcResponse.userId())
                .role(ChatRole.ADMIN)
                .build();

        return chatResponseMapper.toChatResponse(chat,
                chatParticipantResponseMapper,
                chatParticipantRepository);
    }

    @Transactional
    public ChatResponse addParticipant(UUID chatId, String email){
        UUID userId = accountGrpcClient.getUserIdByEmail(email);

        if(userId == null){
           throw new UserNotFound("User with email " + email + " not found");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        if(chat.isDeleted()){
            throw new ChatNotFound("Chat not found");
        }

        if(chat.isGroupChat()){
            throw new ChatNotFound("Chat not found");
        }

        ChatParticipant participant = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.PARTICIPANT)
                .build();
        return chatResponseMapper.toChatResponse(chat,
                chatParticipantResponseMapper,
                chatParticipantRepository);
    }

    @Transactional(readOnly = true)
    public ChatResponse getChat(UUID chatId){
       Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));
       if(chat.isDeleted()){
           throw new ChatNotFound("Chat not found");
       }
       return chatResponseMapper.toChatResponse(chat, chatParticipantResponseMapper, chatParticipantRepository);
    }

    //TODO: агрегированый запрос через gateway чтобы сразу найти по айди юзера
    @Transactional
    public ChatParticipantResponse updateUserRole(UpdateParticipantStatusRequest participantStatusRequest){
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserIdAndChatId(participantStatusRequest.userId()
                ,participantStatusRequest.chatId());

        if(chatParticipant.getChat().isDeleted()){
            throw new ChatNotFound("Chat not found");
        }

        chatParticipant.setRole(participantStatusRequest.newStatus());
        return chatParticipantResponseMapper
                .toChatParticipantResponse(chatParticipant);
    }

    @Transactional
    public void deleteChat(UUID chatId){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        chat.setDeleted(true);
    }

    @Transactional
    public ChatResponse updateChat(UUID chatId){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFound("Chat not found"));

        return null;
    }

}
