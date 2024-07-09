package com.example_login_2.business;

import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.*;
import com.example_login_2.exception.*;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.service.AuthService;
import com.example_login_2.service.EmailConfirmService;
import com.example_login_2.service.JwtTokenService;
import com.example_login_2.util.SecurityUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class AuthBusiness {

    private final AuthService authService;
    private final EmailConfirmService emailConfirmService;
    private final JwtTokenService jwtTokenService;
    private final EmailBusiness emailBusiness;

    public AuthBusiness(EmailBusiness emailBusiness, AuthService authService, EmailConfirmService emailConfirmService, JwtTokenService jwtTokenService) {
        this.emailBusiness = emailBusiness;
        this.authService = authService;
        this.emailConfirmService = emailConfirmService;
        this.jwtTokenService = jwtTokenService;
    }

    public ApiResponse<ModelDTO> register(AuthRegisterRequest request) {
        User user = authService.createUser(request);
        EmailConfirm emailConfirm = emailConfirmService.cerateEmailConfirm(user);
        authService.updateEmailConfirm(user, emailConfirm);

        sendEmail(user, emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    private void sendEmail(User user, EmailConfirm emailConfirm) {
        try {
            emailBusiness.sendActivateUserMail(user, emailConfirm);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        log.info("Token: " + emailConfirm.getToken());
    }

    public ApiResponse<ModelDTO> login(AuthLoginRequest request) {
        User user = authService.getUserByEmail(request.getEmail())
                .orElseThrow(NotFoundException::loginFailEmailNotFound);

        if (!authService.matchPassword(request.getPassword(), user.getPassword()))
            throw UnauthorizedException.loginFailPasswordIncorrect();

        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByUserId(user.getId())
                .orElseThrow(NotFoundException::activateNotFound);

        if (!emailConfirm.isActivated()) throw ForbiddenException.loginFailUserUnactivated();

        JwtToken jwtToken = jwtTokenService.generateJwtToken(user);
        authService.updateJwtToken(user, jwtToken);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(jwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> activate(AuthActivateRequest request) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(request.getToken());

        Date now = new Date();
        Date tokenExpireAt = emailConfirm.getExpiresAt();
        if (now.after(tokenExpireAt)) throw GoneException.activateTokenExpire();

        emailConfirm = emailConfirmService.updateEnableVerificationEmail(emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(emailConfirm.isActivated()));
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<String> resendActivationEmail(AuthResendActivationEmailRequest request) {
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(request.getToken());

        emailConfirm = emailConfirmService.updateEmailConfirm(emailConfirm);

        sendEmail(emailConfirm.getUser(), emailConfirm);
        return new ApiResponse<>(true, "Activation email sent", null);
    }

    public ApiResponse<ModelDTO> refreshJwtToken() {
        User user = validateAndGetUser();
        JwtToken jwtToken = jwtTokenService.generateJwtToken(user);
        authService.updateJwtToken(user, jwtToken);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(jwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> getUserById() {
        User user = validateAndGetUser();
        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(user.getEmailConfirm().isActivated()))
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> updateUser(UpdateRequest request) {
        User user = validateAndGetUser();
        authService.updateUserRequest(user, request);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public void deleteUser() {
        User user = validateAndGetUser();
        authService.deleteUser(user.getId());
    }

    private User validateAndGetUser() {
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(UnauthorizedException::unauthorized);

        return authService.getUserById(userId).orElseThrow(NotFoundException::notFound);
    }

    private EmailConfirm validateAndGetEmailConfirm(String token) {
        if (StringUtil.isNullOrEmpty(token)) throw BadRequestException.requestNoToken();

        EmailConfirm emailConfirm = emailConfirmService.getEmailConfirmByToken(token)
                .orElseThrow(NotFoundException::requestNotFound);

        if (emailConfirm.isActivated()) throw ConflictException.activateAlready();

        return emailConfirm;
    }

}
