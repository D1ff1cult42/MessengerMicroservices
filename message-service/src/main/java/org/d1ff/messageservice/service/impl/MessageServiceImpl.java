package org.d1ff.messageservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.d1ff.messageservice.dto.MinioFileProperties;
import org.d1ff.messageservice.dto.request.CreateMessageRequest;
import org.d1ff.messageservice.dto.request.UpdateMessageRequest;
import org.d1ff.messageservice.dto.response.MessageResponse;
import org.d1ff.messageservice.entity.Message;
import org.d1ff.messageservice.entity.MessageType;
import org.d1ff.messageservice.exceptions.AccessDeniedException;
import org.d1ff.messageservice.exceptions.MessageNotFound;
import org.d1ff.messageservice.grpc.ChatGrpcClient;
import org.d1ff.messageservice.mapper.response.MessageResponseMapper;
import org.d1ff.messageservice.repository.MessageRepository;
import org.d1ff.messageservice.service.interfaces.FileService;
import org.d1ff.messageservice.service.interfaces.MessageService;
import org.d1ff.messageservice.service.interfaces.MessageStatusService;
import org.d1ff.messageservice.utils.ExtensionToTypeConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final FileService fileService;
    private final ChatGrpcClient chatGrpcClient;
    private final MessageRepository messageRepository;
    private final MessageResponseMapper messageResponseMapper;
    private final ExtensionToTypeConverter extensionToTypeConverter;
    private final MessageStatusService messageStatusService;

    //FOR AUTHOR
    @Override
    public MessageResponse sendMessage(CreateMessageRequest createMessageRequest, UUID userId) {
        if (!chatGrpcClient.isUserExistsInChat(userId, createMessageRequest.chatId())) {
            log.error("User {} is not a participant of the chat {}", userId, createMessageRequest.chatId());
            throw new AccessDeniedException("User is not a participant of the chat: " + createMessageRequest.chatId());
        }

        MessageType messageType;
        MinioFileProperties fileProperties = null;
        String fileName = null;
        Long fileSize = null;

        //TODO: Нужно прокинуть в сервис чата grpc запрос с участниками чата и разослать статус sent
        //TODO: Нужно в сервисе нотификации подключить пользователей к вебсокету и проверять статус доставки, подключить через Kafka


        if (createMessageRequest.multipartFile() != null) {
            messageType = extensionToTypeConverter.convert(createMessageRequest.multipartFile().getOriginalFilename());
            fileProperties = fileService.uploadFile(createMessageRequest.multipartFile());
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

        if (fileProperties != null) {
            log.info("Message {} contains file attachment. Object name: {}, Bucket name: {}, Original file name: {}, File size: {} bytes",
                    createMessageRequest.content(), fileProperties.objectName(), fileProperties.bucketName(), fileName, fileSize);
            message.setObjectName(fileProperties.objectName());
            message.setBucketName(fileProperties.bucketName());
            message.setFileName(fileName);
            message.setFileSize(fileSize);
        }

        Message savedMessage = messageRepository.save(message);

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
        log.error("Message {} marked as deleted by user {}", messageId, userId);
    }

    //FOR ADMIN
    @Override
    public void deleteMessageForAdmin(Long messageId){
        log.info("Admin attempting to delete message with ID {}", messageId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));
    }

    //FOR AUTHOR
    @Override
    public MessageResponse updateMessage(UUID userId, UpdateMessageRequest updateMessageRequest) {
        log.info("Attempting to update message with ID {} by user {}", updateMessageRequest.messageId(), userId);
        Message message = messageRepository.findByIdAndNotDeleted(updateMessageRequest.messageId())
                .orElseThrow(() -> new MessageNotFound("Message not found: " + updateMessageRequest.messageId()));

        if (!message.getFromUser().equals(userId)) {
            log.error("User {} is not the author of the message {}. Update denied.", userId, updateMessageRequest.messageId());
            throw new AccessDeniedException("User is not the author of the message: " + updateMessageRequest.messageId());
        }

        message.setContent(updateMessageRequest.content());
        message.setIsEdited(true);
        message.setUpdatedAt(LocalDateTime.now());

        log.info("Message with ID {} updated by user {}. New content: {}", updateMessageRequest.messageId(), userId, updateMessageRequest.content());
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
            return messageResponseMapper.toMessageResponse(message);
        }
        log.error("User {} is not the author of the message {}. Access denied.", userId, messageId);
        throw new AccessDeniedException("User has no access to message: " + messageId);
    }

    //FOR ADMIN
    @Override
    public MessageResponse getMessageByIdForAdmin(Long messageId) {
        log.info("Admin fetching message with ID {}", messageId);
        return messageRepository.findById(messageId)
                .map(messageResponseMapper::toMessageResponse)
                .orElseThrow(() -> new MessageNotFound("Message not found: " + messageId));
    }
}