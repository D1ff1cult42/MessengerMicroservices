package com.d1ff.analyticservice.repository;

import com.d1ff.analyticservice.model.MessageAnalyticEvent;
import com.d1ff.analyticservice.repository.abstractClass.BatchClickHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MessageAnalyticRepository extends
        BatchClickHouseRepository<MessageAnalyticEvent> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void persist(List<MessageAnalyticEvent> batch) {
        jdbcTemplate.batchUpdate(
                """
                INSERT INTO message_events
                    (message_id, user_id, chat_id, event_type, ip_address,
user_agent, char_number, have_file, country, occurred_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                batch,
                batch.size(),
                (ps, e) -> {
                    ps.setLong(1, e.messageId() != null ? e.messageId() : 0L);
                    ps.setString(2, e.userId() != null ? e.userId().toString()
                            : "00000000-0000-0000-0000-000000000000");
                    ps.setString(3, e.chatId() != null ? e.chatId().toString()
                            : "00000000-0000-0000-0000-000000000000");
                    ps.setString(4, e.eventType());
                    ps.setString(5, e.ipAddress() != null ? e.ipAddress() :
                            "");
                    ps.setString(6, e.userAgent() != null ? e.userAgent() :
                            "");
                    ps.setLong(7, e.charNumber() != null ? e.charNumber() :
                            0L);
                    ps.setBoolean(8, e.haveFile());
                    ps.setString(9, e.country() != null ? e.country() : "");
                    ps.setTimestamp(10, e.occurredAt() != null
                            ? Timestamp.from(e.occurredAt())
                            : Timestamp.from(Instant.now()));
                }
        );
    }

    @Override
    protected String tableName() {
        return "message_events";
    }
}
