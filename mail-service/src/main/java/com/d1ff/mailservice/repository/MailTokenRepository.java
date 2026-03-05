package com.d1ff.mailservice.repository;

import com.d1ff.mailservice.entity.MailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MailTokenRepository extends JpaRepository<MailToken, Long> {
    Optional<MailToken> findByToken(UUID token);

    List<MailToken> findAllByUsedFalseAndWarningSentFalseAndCreatedAtBefore(LocalDateTime threshold);

    @Modifying
    @Transactional
    @Query("DELETE FROM MailToken m WHERE m.used = true OR m.expiresAt <= :threshold")
    void deleteExpiredAndUsed(@Param("threshold") LocalDateTime threshold);
}
