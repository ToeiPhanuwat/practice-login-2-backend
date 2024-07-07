package com.example_login_2.repository;

import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<User, Long> {
}
