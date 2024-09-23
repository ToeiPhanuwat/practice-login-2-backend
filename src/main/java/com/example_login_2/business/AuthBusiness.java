package com.example_login_2.business;

import com.example_login_2.config.CustomUserDetails;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.*;
import com.example_login_2.mapper.UserMapper;
import com.example_login_2.model.*;
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
    private final AddressService addressService;
    private final JwtBlacklistService jwtBlacklistService;
    private final EmailBusiness emailBusiness;
    private final UserMapper userMapper;

    public AuthBusiness(EmailBusiness emailBusiness, AuthService authService, EmailConfirmService emailConfirmService, JwtTokenService jwtTokenService, StorageService storageService, AddressService addressService, JwtBlacklistService jwtBlacklistService, UserMapper userMapper) {
        this.emailBusiness = emailBusiness;
        this.authService = authService;
        this.emailConfirmService = emailConfirmService;
        this.jwtTokenService = jwtTokenService;
        this.storageService = storageService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.addressService = addressService;
        this.userMapper = userMapper;
    }

    public ApiResponse<ModelDTO> register(RegisterRequest request) {
        User user = authService.createUser(request);

        EmailConfirm emailConfirm = emailConfirmService.createEmailConfirm(user);
        user = authService.updateEmailConfirm(user, emailConfirm);

        Address address = addressService.createAddress(user);
        user = authService.updateAddress(user, address);

        sendActivationEmail(user, emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setActivationToken(emailConfirm.getToken())
                .setActivated(String.valueOf(emailConfirm.isActivated()));
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
        User user = authService.getUserByEmail(request.getEmail())
                .orElseThrow(NotFoundException::loginFailEmailNotFound);

        if (!authService.matchPassword(request.getPassword(), user.getPassword()))
            throw UnauthorizedException.loginFailPasswordIncorrect();

        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByUserId(user.getId())
                .orElseThrow(NotFoundException::activateNotFound);

        if (!emailConfirm.isActivated()) throw ForbiddenException.loginFailUserUnactivated();

        JwtToken newJwtToken = jwtTokenService.generateJwtToken(user);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(newJwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> activate(String token) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);

        Date now = new Date();
        Date tokenExpireAt = emailConfirm.getExpiresAt();
        if (now.after(tokenExpireAt)) throw GoneException.activateTokenExpire();

        emailConfirm = emailConfirmService.updateEnableVerificationEmail(emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<String> logout() {
        JwtToken currentToken = jwtTokenService.getCurrentToken();

        final String ACTION = "logout";
        jwtTokenService.revokedToken(currentToken);
        jwtBlacklistService.saveToBlacklist(currentToken, ACTION);

        User user = currentToken.getUser();
        if (user == null) throw NotFoundException.handleNoUserInTheToken();
        authService.removeJwtToken(user);

        return new ApiResponse<>(true, "Logged out successfully!", null);
    }

    public ApiResponse<String> resendActivationEmail(String token) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);

        emailConfirm = emailConfirmService.updateEmailConfirm(emailConfirm);

        sendActivationEmail(emailConfirm.getUser(), emailConfirm);
        return new ApiResponse<>(true, "Activation email sent", null);
    }

    public ApiResponse<ModelDTO> forgotPassword(ForgotPasswordRequest request) {

        User user = authService.getUserByEmail(request.getEmail())
                .orElseThrow(NotFoundException::emailNotFound);

        user = authService.updatePasswordResetToken(user);

        PasswordResetToken passwordResetToken = user.getPasswordResetToken();

        sendEmailResetPassword(user, passwordResetToken);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setToken(passwordResetToken.getToken());
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
        User user = authService.getByPasswordResetToken_Token(request.getToken())
                .orElseThrow(NotFoundException::tokenNotFound);

        PasswordResetToken passwordResetToken = user.getPasswordResetToken();
        Instant now = Instant.now();
        Instant passwordExpireAt = passwordResetToken.getExpiresAt();
        if (now.isAfter(passwordExpireAt)) throw GoneException.activateTokenExpire();

        authService.updateNewPassword(user, request.getNewPassword());
        return new ApiResponse<>(true, "Password has been reset successfully.", null);
    }

    public ApiResponse<ModelDTO> refreshJwtToken() {
        JwtToken currentToken = jwtTokenService.getCurrentToken();

        final String ACTION = "refresh_token";
        jwtTokenService.revokedToken(currentToken);
        jwtBlacklistService.saveToBlacklist(currentToken, ACTION);

        User user = currentToken.getUser();
        if (user == null) throw NotFoundException.handleNoUserInTheToken();
        authService.removeJwtToken(user);
        JwtToken newJwtToken = jwtTokenService.generateJwtToken(user);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(newJwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<MUserResponse> getUserById() {
        User user = jwtTokenService.getCurrentUserByToken();

        MUserResponse mUserResponse = userMapper.toUserResponse(user);

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<User> updateUser(UpdateRequest request) {
        User user = jwtTokenService.getCurrentUserByToken();

        user = authService.updateUserRequest(user, request);

        Address address = addressService.updateAddress(user, request);
        user = authService.updateAddress(user, address);

        return new ApiResponse<>(true, "Operation completed successfully", user);
    }

    public ApiResponse<String> updateUser(MultipartFile file) {
        User user = jwtTokenService.getCurrentUserByToken();

        String fileName = storageService.uploadProfilePicture(file);
        if (Objects.isNull(fileName)) throw BadRequestException.noFile();

        authService.updateFile(user, fileName);
        return new ApiResponse<>(true, "Operation completed successfully", null);
    }

    public void deleteUser() {
        User user = jwtTokenService.getCurrentUserByToken();
        authService.deleteUser(user.getId());
    }

    public Resource getImage(String file) throws MalformedURLException {
        Path filePath = Paths.get("images").resolve(file).normalize();
        return new UrlResource(filePath.toUri());
    }

    private CustomUserDetails validateAndGetUserDetails() {
        return SecurityUtil.getCurrentUserDetails()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not authenticated"));
    }

    private EmailConfirm validateAndGetEmailConfirm(String token) {
        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByToken(token)
                .orElseThrow(NotFoundException::requestNotFound);
        log.info("Token Email Confirm: " + token);

        if (emailConfirm.isActivated()) throw ConflictException.activateAlready();
        log.info("Check email verification status: " + false);

        return emailConfirm;
    }

}
