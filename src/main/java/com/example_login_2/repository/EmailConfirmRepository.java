package com.example_login_2.repository;

import com.example_login_2.model.EmailConfirm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmRepository extends JpaRepository<EmailConfirm, Long> {

    Optional<EmailConfirm> findByToken(String token);

    Optional<EmailConfirm> findByUserId(Long userId);
}
