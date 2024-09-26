package com.example_login_2.service;

import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;
import com.example_login_2.repository.EmailConfirmRepository;
import com.example_login_2.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmailConfirmServiceImp implements EmailConfirmService {

    private final EmailConfirmRepository emailConfirmRepository;

    public EmailConfirmServiceImp(EmailConfirmRepository emailConfirmRepository) {
        this.emailConfirmRepository = emailConfirmRepository;
    }

    @Override
    public EmailConfirm createEmailConfirm(User user) {
        String token = SecurityUtil.generateToken();
        EmailConfirm emailConfirm = new EmailConfirm()
                .setUser(user)
                .setToken(token)
                .setExpiresAt(nextHour());
        return emailConfirmRepository.save(emailConfirm);
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        Optional<EmailConfirm> emailConfirm = emailConfirmRepository.findByToken(token);
        return emailConfirm.map(EmailConfirm::getUser);
    }

    @Override
    public Optional<EmailConfirm> getEmailConfirmByUserId(Long userId) {
        return emailConfirmRepository.findByUserId(userId);
    }

    @Override
    public Optional<EmailConfirm> getEmailConfirmByToken(String token) {
        return emailConfirmRepository.findByToken(token);
    }

    @Override
    public EmailConfirm updateEnableVerificationEmail(EmailConfirm emailConfirm) {
        emailConfirm = emailConfirm.setActivated(true);
        return emailConfirmRepository.save(emailConfirm);
    }

    @Override
    public EmailConfirm updateEmailConfirm(EmailConfirm emailConfirm) {
        emailConfirm = emailConfirm
                .setToken(SecurityUtil.generateToken())
                .setExpiresAt(nextHour());
        return emailConfirmRepository.save(emailConfirm);
    }

    @Override
    public List<User> getUserActivatedFalse() {
        return emailConfirmRepository.findByActivatedFalse();
    }

    private Date nextHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTime();
    }
}
