package com.d1ff.messageservice.repository;

import com.d1ff.messageservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAllByFromUser(UUID userId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.id= :id AND m.deleted = false")
    Optional<Message> findByIdAndNotDeleted(@Param("id") Long id);
    Page<Message> findAllByChatId(UUID chatId, Pageable pageable);
}