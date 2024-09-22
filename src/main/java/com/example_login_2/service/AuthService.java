package com.example_login_2.service;

import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

public interface AuthService {
    User createUser(RegisterRequest request);

    User updateUser(User user);

    void updateFile(User user, String fileName);

    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    Optional<User> getUserById(Long id);

    @CachePut(value = "user", key = "#user.id")
    User updateUserRequest(User user, UpdateRequest request);

    User updateEmailConfirmAndAddress(User user, EmailConfirm emailConfirm, Address address);

    User updateEmailConfirm(User user, EmailConfirm emailConfirm);

    User updateJwtToken(User user, JwtToken jwtToken);

    void removeJwtToken(User user);

    @CachePut(value = "user", key = "#user.id")
    void updateNewPassword(User user, String newPassword);

    User updatePasswordResetToken(User user);

    @CachePut(value = "user", key = "#user.id")
    User updateAddress(User user, Address address);

    Optional<User> getUserByEmail(String email);

    Boolean matchPassword(String rawPassword, String encodedPassword);

    Optional<User> getByPasswordResetToken_Token(String token);

    @CacheEvict(value = "user", key = "#id")
    void deleteUser(Long id);

}
