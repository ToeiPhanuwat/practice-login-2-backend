package com.example_login_2.service;

import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;

import java.util.List;
import java.util.Optional;

public interface EmailConfirmService {

    EmailConfirm cerateEmailConfirm(User user);

    Optional<User> getUserByToken(String token);

    Optional<EmailConfirm> getEmailConfirmByUserId(Long userId);

    Optional<EmailConfirm> getEmailConfirmByToken(String token);

    EmailConfirm updateEmailConfirm(EmailConfirm emailConfirm);

    EmailConfirm updateEnableVerificationEmail(EmailConfirm emailConfirm);

    List<User> getUserActivatedFalse();
}
