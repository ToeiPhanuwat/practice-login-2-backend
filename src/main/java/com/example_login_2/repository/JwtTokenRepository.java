package com.example_login_2.repository;

import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    Optional<JwtToken> findByJwtToken(String token);

    @Query("SELECT j.user FROM JwtToken j WHERE j.jwtToken = :jwtToken")
    Optional<User> findUserByJwtToken(@Param("jwtToken") String jwtToken);

    Optional<JwtToken> findByUserId(Long userId);

    @Query("SELECT j FROM JwtToken j WHERE j.expiresAt < :currentTime AND j.isRevoked = false")
    List<JwtToken> findExpiredTokens(@Param("currentTime") Instant currentTime);
}
