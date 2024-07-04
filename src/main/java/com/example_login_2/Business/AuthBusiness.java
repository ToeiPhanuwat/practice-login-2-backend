package com.example_login_2.Business;

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

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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
        authService.updateEmailConfirmUser(user, emailConfirm);

        sendEmail(user, emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setActivated(false);
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    private Date nextHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTime();
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
        Optional<User> optUser = authService.getUserByEmail(request.getEmail());
        if (optUser.isEmpty()) throw NotFoundException.loginFailEmailNotFound();

        User user = optUser.get();
        if (!authService.matchPassword(request.getPassword(), user.getPassword()))
            throw UnauthorizedException.loginFailPasswordIncorrect();

        Optional<EmailConfirm> optEmailConfirm = emailConfirmService.getEmailConfirmByUserId(user.getId());
        if (optEmailConfirm.isEmpty()) throw NotFoundException.activateNotFound();

        EmailConfirm emailConfirm = optEmailConfirm.get();
        if (!emailConfirm.isActivated()) throw ForbiddenException.loginFailUserUnactivated();

        JwtToken jwtToken = createAndRevokeTokens(user);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(jwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> activate(AuthActivateRequest request) {
        String token = request.getToken();
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);

        Date now = new Date();
        Date tokenExpireAt = emailConfirm.getExpiresAt();
        if (now.after(tokenExpireAt)) throw GoneException.activateTokenExpire();
        emailConfirm.setActivated(true);
        emailConfirmService.updateEmailConfirm(emailConfirm);

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(true);
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<String> resendActivationEmail(AuthResendActivationEmailRequest request) {
        String token = request.getToken();
        EmailConfirm emailConfirm = validateAndGetEmailConfirm(token);
        emailConfirm
                .setToken(SecurityUtil.generateToken())
                .setExpiresAt(nextHour());
        emailConfirmService.updateEmailConfirm(emailConfirm);
        sendEmail(emailConfirm.getUser(), emailConfirm);
        return new ApiResponse<>(true, "Activation email sent", null);
    }

    public ApiResponse<ModelDTO> refreshJwtToken() {
        User user = validateAndGetUser();
        JwtToken jwtToken = createAndRevokeTokens(user);

        ModelDTO modelDTO = new ModelDTO()
                .setJwtToken(jwtToken.getJwtToken());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> getUserById() {
        User user = validateAndGetUser();
        ModelDTO modelDTO = new ModelDTO()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> updateUser(AuthUpdateRequest request) {
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

    private JwtToken createAndRevokeTokens(User user) {
        user.revokeAllJwtToken();
        authService.updateUser(user);

        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofMinutes(30));
        String jwt = jwtTokenService.tokenize(user, now, expireAt);
        JwtToken jwtToken = jwtTokenService.createJwtToken(user, jwt, now, expireAt);
        authService.updateJwtUser(user, jwtToken);
        return jwtToken;
    }

    private User validateAndGetUser() {
        Optional<Long> optId = SecurityUtil.getCurrentUserId();
        if (optId.isEmpty()) throw UnauthorizedException.unauthorized();

        Long userId = optId.get();

        Optional<User> optUser = authService.getUserById(userId);
        if (optUser.isEmpty()) throw NotFoundException.notFound();

        return optUser.get();
    }

    private EmailConfirm validateAndGetEmailConfirm(String token) {
        if (StringUtil.isNullOrEmpty(token)) throw BadRequestException.requestNoToken();

        Optional<EmailConfirm> optEmailConfirm = emailConfirmService.getEmailConfirmByToken(token);
        if (optEmailConfirm.isEmpty()) throw NotFoundException.requestNotFound();

        EmailConfirm emailConfirm = optEmailConfirm.get();
        if (emailConfirm.isActivated()) throw ConflictException.activateAlready();

        return emailConfirm;
    }

}
