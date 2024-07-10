package com.example_login_2.service;

import com.example_login_2.controller.request.AuthRegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

public interface AuthService {
    User createUser(AuthRegisterRequest request);

    User updateUser(User user);

    @CachePut(value = "user", key = "#user.id")
    User updateUserRequest(User user, UpdateRequest request);

    User updateEmailConfirm(User user, EmailConfirm emailConfirm);

    User updateJwtToken(User user, JwtToken jwtToken);

    @Cacheable(value = "user", key = "#email")
    Optional<User> getUserByEmail(String email);

    Boolean matchPassword(String rawPassword, String encodedPassword);

    @Cacheable(value = "user", key = "#id")
    Optional<User> getUserById(Long id);

    @CacheEvict(value = "user", key = "#id")
    void deleteUser(Long id);

}
