package com.example_login_2.business;

import com.example_login_2.config.CustomUserDetails;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.ForgotPasswordRequest;
import com.example_login_2.controller.AuthRequest.LoginRequest;
import com.example_login_2.controller.AuthRequest.PasswordResetRequest;
import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.*;
import com.example_login_2.mapper.UserMapper;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
import com.example_login_2.service.*;
import com.example_login_2.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Service
@Log4j2
public class AuthBusiness {

    private final AuthService authService;
    private final EmailConfirmService emailConfirmService;
    private final JwtTokenService jwtTokenService;
    private final StorageService storageService;
    private final JwtBlacklistService jwtBlacklistService;
    private final EmailBusiness emailBusiness;
    private final UserMapper userMapper;

    public AuthBusiness(EmailBusiness emailBusiness, AuthService authService, EmailConfirmService emailConfirmService, JwtTokenService jwtTokenService, StorageService storageService, JwtBlacklistService jwtBlacklistService, UserMapper userMapper) {
        this.emailBusiness = emailBusiness;
        this.authService = authService;
        this.emailConfirmService = emailConfirmService;
        this.jwtTokenService = jwtTokenService;
        this.storageService = storageService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.userMapper = userMapper;
    }

    public ApiResponse<ModelDTO> register(RegisterRequest request) {
        log.info("Starting registration process for email: {}", request.getEmail());
        User user = authService.createUser(request);

        EmailConfirm emailConfirm = emailConfirmService.createEmailConfirm(user);
        user = authService.updateEmailConfirm(user, emailConfirm);

        sendActivationEmail(user, emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setActivationToken(emailConfirm.getToken())
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        log.info("Registration process completed for email: {}", user.getEmail());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    private void sendActivationEmail(User user, EmailConfirm emailConfirm) {
        try {
            emailBusiness.sendActivateUserMail(user, emailConfirm);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        log.info("Token: " + emailConfirm.getToken());
        log.info("The activation email has been successfully sent.");
    }

    public ApiResponse<ModelDTO> login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = authService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: email not found for {}", request.getEmail());
                    return NotFoundException.loginFailEmailNotFound();
                });

        if (!authService.matchPassword(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: incorrect password for email: {}", request.getEmail());
            throw UnauthorizedException.loginFailPasswordIncorrect();
        }

        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByUserId(user.getId())
                .orElseThrow(NotFoundException::activateNotFound);

        if (!emailConfirm.isActivated()) {
            log.warn("Login failed: user not activated for email: {}", request.getEmail());
            throw ForbiddenException.loginFailUserUnactivated();
        }

        JwtToken newJwtToken = jwtTokenService.generateJwtToken(user);
        log.info("User logged in successfully: {}", request.getEmail());

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(newJwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> activate(String token) {
        log.info("Activation attempt with token: {}", token);
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);

        Date now = new Date();
        Date tokenExpireAt = emailConfirm.getExpiresAt();
        if (now.after(tokenExpireAt)) {
            log.warn("Activation failed: token expired for token: {}", token);
            throw GoneException.activateTokenExpire();
        }

        emailConfirm = emailConfirmService.updateEnableVerificationEmail(emailConfirm);
        log.info("Activation successful for email: {}", emailConfirm.getUser().getEmail());

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<String> logout() {
        JwtToken currentToken = jwtTokenService.getCurrentToken();
        log.info("User logout attempt with token_ID: {}", currentToken.getId());

        final String ACTION = "logout";
        jwtTokenService.revokedToken(currentToken);
        jwtBlacklistService.saveToBlacklist(currentToken, ACTION);

        User user = currentToken.getUser();
        if (user == null) {
            log.error("Logout failed: No user associated with the token");
            throw NotFoundException.handleNoUserInTheToken();
        }
        authService.deleteJwtIsRevoked(user);
        log.info("User logged out successfully: {}", user.getEmail());

        return new ApiResponse<>(true, "Logged out successfully!", null);
    }

    public ApiResponse<String> resendActivationEmail(String token) {
        log.info("Resend activation email attempt with token: {}", token);
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);

        emailConfirm = emailConfirmService.updateEmailConfirm(emailConfirm);

        sendActivationEmail(emailConfirm.getUser(), emailConfirm);
        log.info("Resend activation email to: {}", emailConfirm.getUser().getEmail());
        return new ApiResponse<>(true, "Activation email sent", null);
    }

    public ApiResponse<ModelDTO> forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        User user = authService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Forgot password failed: email not found for {}", request.getEmail());
                    return NotFoundException.emailNotFound();
                });

        user = authService.updatePasswordResetToken(user);

        PasswordResetToken passwordResetToken = user.getPasswordResetToken();

        sendEmailResetPassword(user, passwordResetToken);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setToken(passwordResetToken.getToken());
        log.info("Password reset email sent to: {}", user.getEmail());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    private void sendEmailResetPassword(User user, PasswordResetToken passwordResetToken) {
        try {
            emailBusiness.sendPasswordReset(user, passwordResetToken);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        log.info("Token: " + passwordResetToken.getToken());
    }

    public ApiResponse<String> resetPassword(PasswordResetRequest request) {
        log.info("Reset password attempt with token: {}", request.getToken());
        User user = authService.getByPasswordResetToken_Token(request.getToken())
                .orElseThrow(() -> {
                    log.warn("Reset password failed: Token not found");
                    return NotFoundException.tokenNotFound();
                });

        PasswordResetToken passwordResetToken = user.getPasswordResetToken();
        Instant now = Instant.now();
        Instant passwordExpireAt = passwordResetToken.getExpiresAt();
        if (now.isAfter(passwordExpireAt)) {
            log.warn("Reset password failed: Token expired");
            throw GoneException.activateTokenExpire();
        }

        authService.updateNewPassword(user, request.getNewPassword());
        log.info("Password reset successfully for email: {}", user.getEmail());
        return new ApiResponse<>(true, "Password has been reset successfully.", null);
    }

    public ApiResponse<ModelDTO> refreshJwtToken() {
        JwtToken currentToken = jwtTokenService.getCurrentToken();
        log.info("Refreshing JWT token for user ID: {}", currentToken.getUser().getId());

        final String ACTION = "refresh_token";
        jwtTokenService.revokedToken(currentToken);
        jwtBlacklistService.saveToBlacklist(currentToken, ACTION);

        User user = currentToken.getUser();
        if (user == null) {
            log.error("Refresh token failed: No user associated with the token");
            throw NotFoundException.handleNoUserInTheToken();
        }

        authService.deleteJwtIsRevoked(user);
        JwtToken newJwtToken = jwtTokenService.generateJwtToken(user);
        log.info("JWT token refreshed successfully for user ID: {}", user.getId());

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(newJwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<MUserResponse> getUserById() {
        log.info("Fetching current user profile");
        User user = jwtTokenService.getCurrentUserByToken();

        MUserResponse mUserResponse = userMapper.toUserResponse(user);
        log.info("Fetched user profile for email: {}", user.getEmail());

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<MUserResponse> updateUser(UpdateRequest request) {
        log.info("Updating user profile with request");
        User user = jwtTokenService.getCurrentUserByToken();

        user = authService.updateUserRequest(user, request);

        MUserResponse mUserResponse = userMapper.toUserResponse(user);
        log.info("User profile updated successfully for email.");

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<String> updateUser(MultipartFile file) {
        log.info("Updating user profile picture");
        User user = jwtTokenService.getCurrentUserByToken();

        String fileName = storageService.uploadProfilePicture(file);
        if (Objects.isNull(fileName)) {
            log.warn("No file provided for profile picture update");
            throw BadRequestException.noFile();
        }

        authService.updateFile(user, fileName);
        log.info("Profile picture updated for user email: {}", user.getEmail());
        return new ApiResponse<>(true, "Operation completed successfully", null);
    }

    public void deleteUser() {
        log.info("Deleting user");
        User user = jwtTokenService.getCurrentUserByToken();
        authService.deleteUser(user.getId());
        log.info("User deleted successfully for email: {}", user.getEmail());
    }

    public Resource getImage(String file) throws MalformedURLException {
        log.info("Fetching image file: {}", file);
        Path filePath = Paths.get("images").resolve(file).normalize();
        return new UrlResource(filePath.toUri());
    }

    private CustomUserDetails validateAndGetUserDetails() {
        return SecurityUtil.getCurrentUserDetails()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not authenticated"));
    }

    private EmailConfirm validateAndGetEmailConfirm(String token) {
        log.info("Validating email confirmation token: {}", token);
        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByToken(token)
                .orElseThrow(() -> {
                    log.warn("Email confirmation token not found: {}", token);
                    return NotFoundException.requestNotFound();
                });

        if (emailConfirm.isActivated()) {
            log.warn("Email already activated for token: {}", token);
            throw ConflictException.activateAlready();
        }
        log.debug("Email confirmation token is valid and not activated yet");

        return emailConfirm;
    }

}
