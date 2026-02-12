package org.d1ff.messageservice.service.interfaces;

import org.d1ff.messageservice.dto.request.CreateMessageRequest;
import org.d1ff.messageservice.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface MessageService {
    //FOR AUTHOR
    @Transactional
    MessageResponse sendMessage(CreateMessageRequest createMessageRequest, UUID userId);

    //FOR AUTHOR
    @Transactional
    void deleteMessage(UUID userId, Long messageId);

    //FOR ADMIN
    @Transactional
    void deleteMessageForAdmin(Long messageId);

    //FOR AUTHOR
    @Transactional
    MessageResponse updateMessage(UUID userId, String newContent, Long messageId);

    //FOR ADMIN
    @Transactional(readOnly = true)
    Page<MessageResponse> getMessagesOfUserForAdmin(UUID userId, Pageable pageable);

    //FOR ADMIN
    @Transactional(readOnly = true)
    Page<MessageResponse> getMessagesInChat(UUID chatId, Pageable pageable);

    //FOR AUTHOR
    @Transactional(readOnly = true)
    MessageResponse getMessageById(UUID userId, Long messageId);

    //FOR ADMIN
    @Transactional(readOnly = true)
    MessageResponse getMessageByIdForAdmin(Long messageId);
}

