package com.d1ff.messageservice.repository;

import com.d1ff.messageservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100BySentFalseOrderByCreatedAtAsc();
}
