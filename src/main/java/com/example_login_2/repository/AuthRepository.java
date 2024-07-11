package com.example_login_2.repository;

import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken_Token(String token);

}
