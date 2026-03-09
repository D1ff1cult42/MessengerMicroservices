package com.d1ff.mailservice.repository;

import com.d1ff.mailservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100BySentFalseOrderByCreatedAtAsc();
}
