package com.d1ff.chatservice.repository;

import com.d1ff.chatservice.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {
}
