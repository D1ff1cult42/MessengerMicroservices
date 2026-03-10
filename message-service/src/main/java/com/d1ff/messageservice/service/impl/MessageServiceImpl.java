package com.d1ff.messageservice.service.impl;

import com.d1ff.messageservice.kafka.producer.MessageSentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.bucket.BucketResolver;
import com.d1ff.messageservice.dto.request.CreateMessageRequest;
import com.d1ff.dto.response.FileUploadResponse;
import com.d1ff.messageservice.dto.response.MessageResponse;
import com.d1ff.messageservice.entity.Message;
import com.d1ff.messageservice.entity.MessageType;
import com.d1ff.messageservice.exceptions.AccessDeniedException;
import com.d1ff.messageservice.exceptions.MessageNotFound;
import com.d1ff.grpc.client.chat.ChatGrpcClient;
import com.d1ff.grpc.client.file.FileGrpcClient;
import com.d1ff.messageservice.mapper.response.MessageResponseMapper;
import com.d1ff.messageservice.repository.MessageRepository;
import com.d1ff.messageservice.service.interfaces.MessageService;
import com.d1ff.messageservice.service.interfaces.MessageStatusService;
import com.d1ff.messageservice.util.FileTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final FileGrpcClient fileService;
    private final ChatGrpcClient chatGrpcClient;
    private final MessageSentProducer messageSentProducer;
    private final MessageRepository messageRepository;
    private final MessageResponseMapper messageResponseMapper;
    private final MessageStatusService messageStatusService;
    private final FileTypeResolver fileTypeResolver;
    private final BucketResolver bucketResolver;

    //FOR AUTHOR
    @Override
    public MessageResponse sendMessage(CreateMessageRequest createMessageRequest, UUID userId) {
        if (!chatGrpcClient.isUserExistsInChat(userId, createMessageRequest.chatId())) {
            log.error("User {} is not a participant of the chat {}", userId, createMessageRequest.chatId());
            throw new AccessDeniedException("User is not a participant of the chat: " + createMessageRequest.chatId());
        }

        MessageType messageType;
        FileUploadResponse fileUploadResponse = null;
        String fileName = null;
        Long fileSize = null;

        //TODO: Нужно прокинуть в сервис чата grpc запрос с участниками чата и разослать статус sent
        //TODO: Нужно в сервисе нотификации подключить пользователей к вебсокету и проверять статус доставки, подключить через Kafka


        if (createMessageRequest.multipartFile() != null) {
            messageType = fileTypeResolver.resolveMessageType(createMessageRequest.multipartFile().getOriginalFilename());
            String bucket = bucketResolver.resolveBucket(createMessageRequest.multipartFile().getOriginalFilename());
            fileUploadResponse = fileService.uploadFile(bucket, createMessageRequest.multipartFile());
            fileName = createMessageRequest.multipartFile().getOriginalFilename();
            fileSize = createMessageRequest.multipartFile().getSize();
        } else {
            messageType = MessageType.TEXT;
        }

        Message replyMessage = null;
        if (createMessageRequest.replyTo() != null) {
            replyMessage = messageRepository.findByIdAndNotDeleted(createMessageRequest.replyTo())
                    .orElseThrow(() -> new MessageNotFound("Reply message not found: " + createMessageRequest.replyTo()));
            log.error("Reply message {} not found for message {}", createMessageRequest.replyTo(), createMessageRequest.content());
        }

        Message message = Message.builder()
                .chatId(createMessageRequest.chatId())
                .fromUser(userId)
                .content(createMessageRequest.content())
                .replyTo(replyMessage)
                .type(messageType)
                .build();

        if (fileUploadResponse != null) {
            log.info("Message {} contains file attachment. Object name: {}, Bucket name: {}, Original file name: {}, File size: {} bytes",
                    createMessageRequest.content(), fileUploadResponse.objectName(), fileUploadResponse.bucketName(), fileName, fileSize);
            message.setObjectName(fileUploadResponse.objectName());
            message.setBucketName(fileUploadResponse.bucketName());
            message.setFileName(fileName);
            message.setFileSize(fileSize);
        }

        Message savedMessage = messageRepository.save(message);
        messageSentProducer.send(savedMessage, com.d1ff.common.avro.MessageType.CREATED);

        log.info("Message saved with ID {}. Initializing message statuses for all chat participants.", savedMessage.getId());
        messageStatusService.initializeStatusesForNewMessage(savedMessage);

        return messageResponseMapper.toMessageResponse(savedMessage);
    }



    //FOR AUTHOR
    @Override
    public void deleteMessage(UUID userId, Long messageId) {
        log.info("Attempting to delete message with ID {} by user {}", messageId, userId);
        Message message = messageRepository.findByIdAndNotDeleted(messageId)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));

        if (!message.getFromUser().equals(userId)) {
            log.error("User {} is not the author of the message {}. Deletion denied.", userId, messageId);
            throw new AccessDeniedException("User is not the author of the message: " + messageId);
        }

        message.setDeletedAt(LocalDateTime.now());
        message.setDeleted(true);
        messageSentProducer.send(message, com.d1ff.common.avro.MessageType.DELETED);
        log.info("Message {} marked as deleted by user {}", messageId, userId);
    }

    //FOR ADMIN
    @Override
    public void deleteMessageForAdmin(Long messageId){
        log.info("Admin attempting to delete message with ID {}", messageId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));

        message.setDeletedAt(LocalDateTime.now());
        message.setDeleted(true);
        messageSentProducer.send(message, com.d1ff.common.avro.MessageType.DELETED);
        log.info("Message {} marked as deleted by admin", messageId);
    }

    //FOR AUTHOR
    @Override
    public MessageResponse updateMessage(UUID userId, String newContent, Long messageId) {

        log.info("Attempting to update message with ID {} by user {}", messageId, userId);
        Message message = messageRepository.findByIdAndNotDeleted(messageId)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));

        if (!message.getFromUser().equals(userId)) {
            log.error("User {} is not the author of the message {}. Update denied.", userId, messageId);
            throw new AccessDeniedException("User is not the author of the message: " + messageId);
        }

        message.setContent(newContent);
        message.setIsEdited(true);
        message.setUpdatedAt(LocalDateTime.now());
        messageSentProducer.send(message, com.d1ff.common.avro.MessageType.UPDATED);

        log.info("Message with ID {} updated by user {}. New content: {}", messageId, userId, newContent);
        return messageResponseMapper.toMessageResponseWithUrl(message, fileService);
    }

    //FOR ADMIN
    @Override
    public Page<MessageResponse> getMessagesOfUserForAdmin(UUID userId, Pageable pageable) {
        log.info("Admin fetching messages for user {}", userId);
        return messageRepository.findAllByFromUser(userId, pageable)
                .map(messageResponseMapper::toMessageResponse);
    }

    //FOR ADMIN
    @Override
    public Page<MessageResponse> getMessagesInChat(UUID chatId, Pageable pageable) {
        log.info("Admin fetching messages for chat {}", chatId);
        return messageRepository.findAllByChatId(chatId, pageable)
                .map(messageResponseMapper::toMessageResponse);
    }

    //FOR AUTHOR
    @Override
    public MessageResponse getMessageById(UUID userId, Long messageId) {
        log.info("Attempting to fetch message with ID {} by user {}", messageId, userId);
        Message message = messageRepository.findByIdAndNotDeleted(messageId)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));

        if (message.getFromUser().equals(userId)) {
            log.info("User {} is the author of the message {}. Fetching message details.", userId, messageId);
            return messageResponseMapper.toMessageResponseWithUrl(message, fileService);
        }
        log.error("User {} is not the author of the message {}. Access denied.", userId, messageId);
        throw new AccessDeniedException("User has no access to message: " + messageId);
    }

    //FOR ADMIN
    @Override
    public MessageResponse getMessageByIdForAdmin(Long messageId) {
        log.info("Admin fetching message with ID {}", messageId);
        return messageRepository.findById(messageId)
                .map(message -> messageResponseMapper.toMessageResponseWithUrl(message, fileService))
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));
    }
}