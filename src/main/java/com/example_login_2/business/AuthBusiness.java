package com.example_login_2.business;

import com.example_login_2.config.CustomUserDetails;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.*;
import com.example_login_2.model.*;
import com.example_login_2.service.*;
import com.example_login_2.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
public class AuthBusiness {

    private final AuthService authService;
    private final EmailConfirmService emailConfirmService;
    private final JwtTokenService jwtTokenService;
    private final StorageService storageService;
    private final AddressService addressService;
    private final EmailBusiness emailBusiness;
    private final JwtBlacklistService jwtBlacklistService;

    public AuthBusiness(EmailBusiness emailBusiness, AuthService authService, EmailConfirmService emailConfirmService, JwtTokenService jwtTokenService, StorageService storageService, AddressService addressService, JwtBlacklistService jwtBlacklistService) {
        this.emailBusiness = emailBusiness;
        this.authService = authService;
        this.emailConfirmService = emailConfirmService;
        this.jwtTokenService = jwtTokenService;
        this.storageService = storageService;
        this.addressService = addressService;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    public ApiResponse<ModelDTO> register(RegisterRequest request) {
        User user = authService.createUser(request);

        EmailConfirm emailConfirm = emailConfirmService.cerateEmailConfirm(user);
        user = authService.updateEmailConfirm(user, emailConfirm);

        Address address = addressService.createAddress(user);
        user = authService.updateAddress(user, address);

//        JwtToken jwtToken = new JwtToken();
//        user = authService.updateJwtToken(user, jwtToken);

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

    public ApiResponse<ModelDTO> activate(ActivateRequest request) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(request.getToken());

        Date now = new Date();
        Date tokenExpireAt = emailConfirm.getExpiresAt();
        if (now.after(tokenExpireAt)) throw GoneException.activateTokenExpire();

        emailConfirm = emailConfirmService.updateEnableVerificationEmail(emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<String> logout() {
        JwtToken currentToken = validateAndGetToken();

        final String ACTION = "logout";
        jwtTokenService.revokedToken(currentToken);
        jwtBlacklistService.saveToBlacklist(currentToken, ACTION);

        return new ApiResponse<>(true, "Logged out successfully!", null);
    }

    public ApiResponse<String> resendActivationEmail(ResendActivationEmailRequest request) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(request.getToken());

        emailConfirm = emailConfirmService.updateEmailConfirm(emailConfirm);

//        authService.updateEmailConfirm(emailConfirm.getUser(), emailConfirm);

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
        JwtToken currentToken = validateAndGetToken();

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

    public ApiResponse<ModelDTO> getUserById() {
        User user = validateAndGetUser();
        Address address = user.getAddress();
        String isActivated = String.valueOf(user.getEmailConfirm().isActivated());

        ModelDTO modelDTO = new ModelDTO();
        modelDTO
                .setActivated(isActivated)
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender())
                .setFileName(user.getFileName())
                .setRole(user.getRoles().toString())
                .setAddress(address.getAddress())
                .setCity(address.getCity())
                .setStateProvince(address.getStateProvince())
                .setPostalCode(address.getPostalCode())
                .setCountry(address.getCountry());

        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

//    public ApiResponse<ModelDTO> getUserById() {
//        User user = validateAndGetUser();
//        Address address = user.getAddress();
//        String isActivated = String.valueOf(user.getEmailConfirm().isActivated());
//
//        ModelDTO modelDTO = new ModelDTO();
//        modelDTO
//                .setActivated(isActivated)
//                .setFirstName(user.getFirstName())
//                .setLastName(user.getLastName())
//                .setPhoneNumber(user.getPhoneNumber())
//                .setDateOfBirth(user.getDateOfBirth())
//                .setGender(user.getGender())
//                .setFileName(user.getFileName())
//                .setRole(user.getRoles().toString())
//                .setAddress(address.getAddress())
//                .setCity(address.getCity())
//                .setStateProvince(address.getStateProvince())
//                .setPostalCode(address.getPostalCode())
//                .setCountry(address.getCountry());
//
//        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
//    }

    public ApiResponse<ModelDTO> updateUser(MultipartFile file, UpdateRequest request) {
        User user = validateAndGetUser();

//        if (file != null && !file.isEmpty()) {
//            request.setFileName(storageService.uploadProfilePicture(file));
//        }

        request.setFileName(storageService.uploadProfilePicture(file));

        user = authService.updateUserRequest(user, request);

        Address address = addressService.updateAddress(user, request);
        user = authService.updateAddress(user, address);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender())
                .setFileName(user.getFileName())
                .setAddress(address.getAddress())
                .setCity(address.getCity())
                .setStateProvince(address.getStateProvince())
                .setPostalCode(address.getPostalCode())
                .setCountry(address.getCountry());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public void deleteUser() {
        User user = validateAndGetUser();
        authService.deleteUser(user.getId());
    }

    private User validateAndGetUser() {
        long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(UnauthorizedException::unauthorized);
        return authService.getUserById(userId).orElseThrow(NotFoundException::notFound);
    }

    private JwtToken validateAndGetToken() {
        String token = SecurityUtil.getCurrentToken()
                .orElseThrow(UnauthorizedException::unauthorized);
        return jwtTokenService.getJwtToken(token).orElseThrow(NotFoundException::tokenNotFound);
    }

    private CustomUserDetails validateAndGetUserDetails() {
        return SecurityUtil.getCurrentUserDetails()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not authenticated"));
    }

    private EmailConfirm validateAndGetEmailConfirm(String token) {
        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByToken(token)
                .orElseThrow(NotFoundException::requestNotFound);

        if (emailConfirm.isActivated()) throw ConflictException.activateAlready();

        return emailConfirm;
    }

}
