package com.example_login_2.service;

import com.example_login_2.controller.request.AuthRegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;

import java.util.Optional;

public interface AuthService {
    User createUser(AuthRegisterRequest request);

    User updateUser(User user);

    User updateUserRequest(User user, UpdateRequest request);

    User updateEmailConfirmUser(User user, EmailConfirm emailConfirm);

    User updateJwtUser(User user, JwtToken jwtToken);

    Optional<User> getUserByEmail(String email);

    Boolean matchPassword(String rawPassword, String encodedPassword);

    Optional<User> getUserById(Long id);

    void deleteUser(Long id);

}
