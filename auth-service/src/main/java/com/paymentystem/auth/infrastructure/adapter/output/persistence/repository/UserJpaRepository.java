package com.paymentystem.auth.infrastructure.adapter.output.persistence.repository;

import com.paymentystem.auth.infrastructure.adapter.output.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("""
        UPDATE UserJpaEntity u
        SET u.failedLoginAttempts = u.failedLoginAttempts + 1,
            u.status = CASE
                WHEN u.failedLoginAttempts + 1 >= 5 THEN 'BLOCKED'
                ELSE u.status
            END,
            u.updatedAt = CURRENT_TIMESTAMP
        WHERE u.id = :userId
    """)
    void incrementFailedAttempts(@Param("userId") String userId);
}