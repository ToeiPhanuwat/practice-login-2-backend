package com.example_login_2.service;

import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
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
public class AuthServiceImp implements AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImp(AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @Override
    public User createUser(RegisterRequest request) {
        if (authRepository.existsByEmail(request.getEmail())) {
            log.warn("Attempt to create duplicate user with email: {}", request.getEmail());
            throw ConflictException.createDuplicate();
        }

        User user = new User()
                .setFirstName(request.getFirstName())
                .setEmail(request.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(request.getPassword()))
                .setRoles(new HashSet<>(Collections.singleton("ROLE_USER")));
        log.info("Saving new user to database with email: {}", request.getEmail());
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public void updateFile(User user, String fileName) {
        user.setFileName(fileName);
        authRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return authRepository.findById(id);
    }

    @Transactional
    @Override
    public User updateUserRequest(User user, UpdateRequest request) {
        log.info("Updating user with ID: {}", user.getId());
        user = user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .setAddress(request.getAddress());
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public User updateJwtToken(User user, JwtToken newJwtToken) {
        List<JwtToken> tokens = user.getJwtToken();
        tokens.add(newJwtToken);
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteJwtIsRevoked(User user) {
        log.info("Deleting revoked JWT tokens for user ID: {}", user.getId());
        user.getJwtToken().removeIf(JwtToken::isRevoked);
        authRepository.save(user);
        log.info("Revoked tokens deleted successfully for user ID: {}", user.getId());
    }

    @Transactional
    @Override
    public void deleteJwtExpired(User user, JwtToken jwtToken) {
        if (user.getJwtToken().contains(jwtToken)) {
            log.info("Deleting expired JWT token (ID: {}) for user ID: {}", jwtToken.getId() , user.getId());
            user.getJwtToken().remove(jwtToken);
            authRepository.save(user);
            log.info("Expired token (ID: {}) deleted successfully for user ID: {}", jwtToken.getId() , user.getId());
        } else {
            log.error("Tokne (ID: {}) not found for user ID: {}", jwtToken.getId() , user.getId());
            throw NotFoundException.handleNoUserInTheToken();
        }
    }

    @Transactional
    @Override
    public User updateEmailConfirm(User user, EmailConfirm emailConfirm) {
        user = user.setEmailConfirm(emailConfirm);
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public User updatePasswordResetToken(User user) {
        String token = SecurityUtil.generateToken();
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(15));

        log.info("Generated password reset token for user ID: {}", user.getId());

        PasswordResetToken passwordResetToken = new PasswordResetToken()
                .setToken(token)
                .setExpiresAt(expiresAt);

        user = user.setPasswordResetToken(passwordResetToken);
        return authRepository.save(user);
    }

    @Transactional
    @Override
    public void updateNewPassword(User user, String newPassword) {
        log.info("Updating password for user ID: {}", user.getId());
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

    @Transactional
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        authRepository.deleteById(id);
    }

}
