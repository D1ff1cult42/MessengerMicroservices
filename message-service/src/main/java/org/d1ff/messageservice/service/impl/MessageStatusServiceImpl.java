package org.d1ff.messageservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import org.d1ff.messageservice.entity.DeliveryStatus;
import org.d1ff.messageservice.entity.Message;
import org.d1ff.messageservice.entity.MessageStatus;
import org.d1ff.messageservice.exceptions.MessageNotFound;
import org.d1ff.messageservice.grpc.ChatGrpcClient;
import org.d1ff.messageservice.grpc.FileGrpcClient;
import org.d1ff.messageservice.mapper.response.MessageResponseMapper;
import org.d1ff.messageservice.mapper.response.MessageStatusResponseMapper;
import org.d1ff.messageservice.repository.MessageStatusRepository;
import org.d1ff.messageservice.service.interfaces.MessageStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageStatusServiceImpl implements MessageStatusService {

    private final MessageStatusRepository messageStatusRepository;
    private final MessageStatusResponseMapper messageStatusResponseMapper;
    private final MessageResponseMapper messageResponseMapper;
    private final ChatGrpcClient chatGrpcClient;
    private final FileGrpcClient fileService;

    //FOR AUTHOR
    @Override
    public Page<MessageStatusWithoutMessageResponse> getAllStatusForMessage(Long messageId, Pageable pageable) {
        log.info("Getting all statuses for message with id: {}", messageId);
        return messageStatusRepository.findAllByMessageIdAndNotDeleted(messageId, pageable)
                .map(messageStatusResponseMapper::toMessageStatusResponseWithoutMessage);
    }

    @Override
    public void initializeStatusesForNewMessage(Message message) {
        UUID authorId = message.getFromUser();
        log.info("Initializing message statuses for new message with id: {} from user: {}", message.getId(), authorId);
        List<UUID> users = chatGrpcClient.getChatParticipants(message.getChatId());
        users.remove(authorId);
        for(UUID userId : users){
            MessageStatus messageStatus = MessageStatus.builder()
                    .message(message)
                    .userId(userId)
                    .build();
            messageStatusRepository.save(messageStatus);
        }
    }

    //FOR RECIPIENT
    @Override
    public Page<MessageStatusResponse> getMessagesForUserInChat(UUID userId, UUID chatId, Pageable pageable) {
        log.info("Getting messages with statuses for user: {} in chat: {}", userId, chatId);
        Page<MessageStatus> statusPage = messageStatusRepository.findMessageStatusesByUserAndChatAndNotDeleted(userId, chatId, pageable);

        statusPage.getContent().forEach(messageStatus -> {
            if (messageStatus.getStatus() != DeliveryStatus.READ) {
                messageStatus.setStatus(DeliveryStatus.READ);
                messageStatus.setReadAt(LocalDateTime.now());
            }
        });

        return statusPage.map(messageStatus ->
            messageStatusResponseMapper.toMessageStatusResponse(messageStatus, messageResponseMapper, fileService)
        );
    }

    //FOR RECIPIENT
    @Override
    public MessageStatusResponse getMessageWithStatus(UUID userId, Long messageId){
        log.info("Getting message with status for user: {} and message id: {}", userId, messageId);
        MessageStatus messageStatus = messageStatusRepository.findByMessageIdAndUserIdAndNotDeleted(messageId, userId)
                .orElseThrow(() -> new MessageNotFound("message not found!"));
        messageStatus.setStatus(DeliveryStatus.READ);
        messageStatus.setReadAt(LocalDateTime.now());
        return messageStatusResponseMapper.toMessageStatusResponse(messageStatus, messageResponseMapper, fileService);
    }

    //FOR RECIPIENT
    @Override
    public long getUnreadCountForChat(UUID userId, UUID chatId){
        log.info("Getting unread message count for user: {} in chat: {}", userId, chatId);
        return messageStatusRepository.countUnreadMessagesInChatAndNotDeleted(userId, DeliveryStatus.READ, chatId);
    }

    //FOR RECIPIENT
    @Override
    public MessageStatusResponse getLastMessageStatusForUserInChat(UUID userId, UUID chatId){
        log.info("Getting last message status for user: {} in chat: {}", userId, chatId);
        MessageStatus messageStatus = messageStatusRepository.findLatestMessageStatusForUserInChat(userId, chatId)
                .orElseThrow(() -> new MessageNotFound("message not found! : " + chatId));
        return messageStatusResponseMapper.toMessageStatusResponseWithoutUrl(messageStatus, messageResponseMapper);
    }

    //FOR RECIPIENT
    @Override
    public Page<MessageStatusResponse> getAllStatusesFromUserInChat(UUID userId, UUID fromUserId, UUID chatId, Pageable pageable){
        log.info("Getting all message statuses from user: {} in chat: {} for user: {}", fromUserId, chatId, userId);
        List<UUID> participants = chatGrpcClient.getChatParticipants(chatId);
        if(!(participants.contains(userId) && participants.contains(fromUserId))){
            log.error("User {} is not a participant of chat {}. Access denied.", userId, chatId);
            throw new MessageNotFound("Chat not found: " + chatId);
        }
        return messageStatusRepository.findMessageStatusesByFromUserAndChatAndNotDeleted(fromUserId, chatId, pageable)
                .map(messageStatus -> messageStatusResponseMapper.toMessageStatusResponse(messageStatus, messageResponseMapper, fileService));
    }

    //FOR ADMIN
    @Override
    public Page<MessageStatusResponse> getAllStatusesForMessageForAdmin(UUID fromUserId, UUID chatId, Pageable pageable){
        log.info("Admin getting all message statuses from user: {} in chat: {}", fromUserId, chatId);
        return messageStatusRepository.findMessageStatusesByFromUserAndChatAndNotDeleted(fromUserId, chatId, pageable)
                .map(messageStatus -> messageStatusResponseMapper.toMessageStatusResponseWithoutUrl(messageStatus, messageResponseMapper));
    }
}
