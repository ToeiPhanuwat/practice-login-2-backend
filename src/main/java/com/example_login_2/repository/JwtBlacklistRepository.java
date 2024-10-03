package com.example_login_2.repository;

import com.example_login_2.model.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {

    Optional<JwtBlacklist> findByToken(String token);

    @Query("SELECT j FROM JwtBlacklist j WHERE j.expiresAt < :currentTime")
    List<JwtBlacklist> findExpiredTokens(@Param("currentTime") Instant currentTime);
}
