package org.d1ff.messageservice.service.interfaces;

import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import org.d1ff.messageservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface MessageStatusService {

    //FOR AUTHOR
    @Transactional(readOnly = true)
    Page<MessageStatusWithoutMessageResponse> getAllStatusForMessage(Long messageId, Pageable pageable);

    @Transactional
    void initializeStatusesForNewMessage(Message message);

    //FOR RECIPIENT
    @Transactional
    Page<MessageStatusResponse> getMessagesForUserInChat(UUID userId, UUID chatId, Pageable pageable);

    //FOR RECIPIENT
    @Transactional
    MessageStatusResponse getMessageWithStatus(UUID userId, Long messageId);

    //FOR RECIPIENT
    @Transactional(readOnly = true)
    long getUnreadCountForChat(UUID userId, UUID chatId);

    //FOR RECIPIENT
    @Transactional(readOnly = true)
    MessageStatusResponse getLastMessageStatusForUserInChat(UUID userId, UUID chatId);

    //FOR RECIPIENT
    @Transactional(readOnly = true)
    Page<MessageStatusResponse> getAllStatusesFromUserInChat(UUID userId, UUID fromUserId, UUID chatId, String role, Pageable pageable);
}