package com.d1ff.analyticservice.repository;

import com.d1ff.analyticservice.model.AuthAnalyticEvent;
import com.d1ff.analyticservice.repository.abstractClass.BatchClickHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class AuthAnalyticRepository extends BatchClickHouseRepository<AuthAnalyticEvent> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void persist(List<AuthAnalyticEvent> batch) {
        jdbcTemplate.batchUpdate(
                """ 
         INSERT INTO auth_events (event_type, user_id, ip_address, user_agent, country, occurred_at) 
         VALUES (?, ?, ?, ?, ?, ?) 
      """,
                batch,
                batch.size(),
                (ps, e) -> {
                    ps.setString(1, e.eventType());
                    ps.setString(2, e.userId() != null
                            ? e.userId().toString()
                            : "00000000-0000-0000-0000-000000000000");
                    ps.setString(3, nullSafe(e.ipAddress()));
                    ps.setString(4, nullSafe(e.userAgent()));
                    ps.setString(5, nullSafe(e.country()));
                    ps.setTimestamp(6, e.occurredAt() != null
                            ? Timestamp.from(e.occurredAt())
                            : Timestamp.from(Instant.now()));
                }
        );
    }

    @Override
    protected String tableName() {
        return "auth_events";
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }
}
