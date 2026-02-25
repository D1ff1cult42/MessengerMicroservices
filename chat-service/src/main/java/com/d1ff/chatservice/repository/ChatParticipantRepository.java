package com.d1ff.chatservice.repository;

import com.d1ff.chatservice.entity.ChatParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {
    //  !!! DON'T FORGET DONT USE CHAT FIELD, IT CAUSED N+1 PROBLEM, ONLY FOR MAPPINGS !!!

    Page<ChatParticipant> findByChatId(UUID chatId, Pageable pageable);

    @Query("SELECT cp FROM ChatParticipant cp LEFT JOIN FETCH cp.chat c WHERE c.id = :chatId AND cp.userId = :userId")
    Optional<ChatParticipant> findByUserIdAndChatId(@Param("userId")UUID userId, @Param("chatId")UUID chatId);

    @Query("SELECT cp FROM ChatParticipant cp LEFT JOIN FETCH cp.chat c WHERE c.id = :chatId AND cp.userId IN :userIds")
    List<ChatParticipant> findAllByUserIdsAndChatId(@Param("userIds") List<UUID> userIds, @Param("chatId") UUID chatId);

    @Query("SELECT cp.userId FROM ChatParticipant cp WHERE cp.chat.id = :chatId")
    List<UUID> findUserIdsByChatId(@Param("chatId") UUID chatId);

    @Query("SELECT cp FROM ChatParticipant cp JOIN FETCH cp.chat WHERE cp.chat.id = :chatId ORDER BY cp.userId")
    List<ChatParticipant> findAllByChatIdWithChat(@Param("chatId") UUID chatId);
}

