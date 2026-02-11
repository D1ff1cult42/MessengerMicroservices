package org.d1ff.messageservice.repository;

import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.entity.DeliveryStatus;
import org.d1ff.messageservice.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {
    @Query("SELECT ms FROM MessageStatus ms LEFT JOIN FETCH ms.message m WHERE m.id = :messageId AND ms.userId = :userId AND m.deleted = false")
    Optional<MessageStatus> findByMessageIdAndUserIdAndNotDeleted(@Param("messageId") Long messageId,@Param("userId") UUID userId);

    @Query("SELECT ms FROM MessageStatus ms LEFT JOIN FETCH ms.message m WHERE m.id = :messageId AND m.deleted = false ORDER BY m.createdAt DESC")
    Page<MessageStatus> findAllByMessageIdAndNotDeleted(@Param("messageId") Long messageId, Pageable pageable);

    @Query("SELECT ms FROM MessageStatus ms LEFT JOIN FETCH ms.message m WHERE ms.userId = :userId AND m.chatId = :chatId AND m.deleted = false ORDER BY m.createdAt DESC")
    Page<MessageStatus> findMessageStatusesByUserAndChatAndNotDeleted(@Param("userId") UUID userId, @Param("chatId") UUID chatId, Pageable pageable);

    @Query("SELECT COUNT(ms) FROM MessageStatus ms JOIN ms.message m WHERE ms.userId = :userId AND ms.status != :status AND m.chatId = :chatId AND m.deleted = false")
    Long countUnreadMessagesInChatAndNotDeleted(@Param("userId") UUID userId, @Param("status") DeliveryStatus status, @Param("chatId") UUID chatId);

    @Query("SELECT ms FROM MessageStatus ms LEFT JOIN FETCH ms.message m WHERE ms.userId = :userId AND m.chatId = :chatId AND m.deleted = false ORDER BY m.createdAt DESC")
    Optional<MessageStatus> findLatestMessageStatusForUserInChat(@Param("userId") UUID userId, @Param("chatId") UUID chatId);

    @Query("SELECT ms FROM MessageStatus ms LEFT JOIN FETCH ms.message m WHERE m.fromUser = :userId AND m.chatId = :chatId AND m.deleted = false ORDER BY m.createdAt DESC")
    Page<MessageStatus> findMessageStatusesByFromUserAndChatAndNotDeleted(@Param("userId") UUID userId, @Param("chatId") UUID chatId, Pageable pageable);
}