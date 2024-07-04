package com.example_login_2.repository;

import com.example_login_2.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    Optional<JwtToken> findByjwtToken(String token);

    Optional<JwtToken> findByUserId(Long userId);
}
