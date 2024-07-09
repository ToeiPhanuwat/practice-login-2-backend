package com.example_login_2.repository;

import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {

    List<User> findByRoles(String roles);

}
