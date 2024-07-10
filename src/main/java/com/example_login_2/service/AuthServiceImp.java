package com.example_login_2.service;

import com.example_login_2.controller.request.AuthRegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.AuthRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;


@Service
public class AuthServiceImp implements AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImp(AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User createUser(AuthRegisterRequest request) {
        if (authRepository.existsByEmail(request.getEmail())) throw ConflictException.createDuplicate();

        final String ROLE = "ROLE_USER";
        User user = new User()
                .setEmail(request.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(request.getPassword()))
                .setRoles(new HashSet<>(Collections.singleton(ROLE)));
        return authRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return authRepository.save(user);
    }

    @Override
    public User updateUserRequest(User user, UpdateRequest request) {
        user = user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .setFileName(request.getFileName());
        return authRepository.save(user);
    }

    @Override
    public User updateJwtToken(User user, JwtToken jwtToken) {
        user = user.setJwtToken(jwtToken);
        return authRepository.save(user);
    }

    @Override
    public User updateEmailConfirm(User user, EmailConfirm emailConfirm) {
        user = user.setEmailConfirm(emailConfirm);
        return authRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    @Override
    public Boolean matchPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return authRepository.findById(id);
    }

    @Override
    public void deleteUser(Long id) {
        authRepository.deleteById(id);
    }

}
