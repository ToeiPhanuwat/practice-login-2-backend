package com.example_login_2.repository;

import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmailConfirmRepository extends JpaRepository<EmailConfirm, Long> {

    Optional<EmailConfirm> findByToken(String token);

    Optional<EmailConfirm> findByUserId(Long userId);

    @Query("SELECT u FROM User u WHERE u.emailConfirm.activated = false")
    List<User> findByActivatedFalse();
}
