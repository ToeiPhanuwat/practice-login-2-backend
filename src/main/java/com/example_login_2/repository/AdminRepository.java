package com.example_login_2.repository;

import com.example_login_2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {

    List<User> findByRoles(String roles);

}
