package com.example_login_2.service;

import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.*;
import com.example_login_2.repository.AuthRepository;
import com.example_login_2.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
@Transactional
public class AuthServiceImp implements AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImp(AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User createUser(RegisterRequest request) {
        if (authRepository.existsByEmail(request.getEmail())) throw ConflictException.createDuplicate();

        final String ROLE = "ROLE_USER";
        User user = new User()
                .setFirstName(request.getFirstName())
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
    public void updateFile(User user, String fileName) {
        user.setFileName(fileName);
        authRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return authRepository.findById(id);
    }

    @Override
    public User updateUserRequest(User user, UpdateRequest request) {
        user = user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .setAddress(request.getAddress());
        return authRepository.save(user);
    }

    @Override
    public User updateJwtToken(User user, JwtToken newJwtToken) {
        List<JwtToken> tokens = user.getJwtToken();
        tokens.add(newJwtToken);
        return authRepository.save(user);
    }

    @Override
    public void deleteJwtIsRevoked(User user) {
        user.getJwtToken().removeIf(JwtToken::isRevoked);
        authRepository.save(user);
    }

    @Override
    public void deleteJwtExpired(User user, JwtToken jwtToken) {
        if (user.getJwtToken().contains(jwtToken)) {
            user.getJwtToken().remove(jwtToken);
            authRepository.save(user);
        } else {
            throw NotFoundException.handleNoUserInTheToken();
        }
    }

    @Override
    public User updateEmailConfirm(User user, EmailConfirm emailConfirm) {
        user = user.setEmailConfirm(emailConfirm);
        return authRepository.save(user);
    }

    @Override
    public User updatePasswordResetToken(User user) {
        String token = SecurityUtil.generateToken();
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(15));

        PasswordResetToken passwordResetToken = new PasswordResetToken()
                .setToken(token)
                .setExpiresAt(expiresAt);

        user = user.setPasswordResetToken(passwordResetToken);
        return authRepository.save(user);
    }

    @Override
    public void updateNewPassword(User user, String newPassword) {
        user = user
                .setPassword(bCryptPasswordEncoder.encode(newPassword))
                .setPasswordResetToken(null);
        authRepository.save(user);
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
    public Optional<User> getByPasswordResetToken_Token(String token) {
        return authRepository.findByPasswordResetToken_Token(token);
    }

    @Override
    public void deleteUser(Long id) {
        authRepository.deleteById(id);
    }

}
