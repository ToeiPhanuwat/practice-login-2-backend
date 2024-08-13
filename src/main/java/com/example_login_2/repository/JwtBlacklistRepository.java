package com.example_login_2.repository;

import com.example_login_2.model.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {

    Optional<JwtBlacklist> findByToken(String token);
}
