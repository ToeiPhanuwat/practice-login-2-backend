package com.example_login_2.business;

import com.example.login.common.EmailRequest;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class EmailBusiness {

    private final KafkaTemplate<String, EmailRequest> kafkaEmailTemplate;

    public EmailBusiness(KafkaTemplate<String, EmailRequest> kafkaEmailTemplate) {
        this.kafkaEmailTemplate = kafkaEmailTemplate;
    }

    public void sendActivateUserMail(User user, EmailConfirm emailConfirm) {
        String html;
        String classPath = "activate-user.html";
        try {
            html = readEmailTemplate(classPath);
        } catch (IOException ex) {
            throw NotFoundException.templateNotFound();
        }

        log.info("Token = " + emailConfirm.getToken());

        String activateLink = "http://localhost:3000/activate/" + emailConfirm.getToken();
        String reEmailLink = "http://localhost:3000/resend-activation-email/" + emailConfirm.getToken();
        html = html.replace("${P_NAME}", user.getFirstName());
        html = html.replace("${ACTIVATE_LINK}", activateLink);
        html = html.replace("${RE_EMAIL_LINK}", reEmailLink);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("Please activate your account");
        emailRequest.setContent(html);

        CompletableFuture<SendResult<String, EmailRequest>> future = kafkaEmailTemplate.send("activation-email", emailRequest);
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                handleFailure(throwable);
            } else {
                handleSuccess(result);
            }
        });
    }

    public void sendPasswordReset(User user, PasswordResetToken passwordResetToken) {
        String html;
        String classPath = "reset-password.html";
        try {
            html = readEmailTemplate(classPath);
        } catch (IOException ex) {
            throw NotFoundException.templateNotFound();
        }

        log.info("Token = " + user.getPasswordResetToken());
        log.info("Email User : " + user.getEmail());

        String passwordResetLink = "http://localhost:3000/reset-password/" + passwordResetToken.getToken();
        html = html.replace("${P_NAME}", user.getFirstName());
        html = html.replace("${EMAIL}", user.getEmail());
        html = html.replace("${PASSWORD_RESET_LINK}", passwordResetLink);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("Reset Password");
        emailRequest.setContent(html);

        CompletableFuture<SendResult<String, EmailRequest>> future = kafkaEmailTemplate.send("password-reset-email", emailRequest);
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                handleFailure(throwable);
            } else {
                handleSuccess(result);
            }
        });
    }

    private void handleFailure(Throwable throwable) {
        log.error("Kafka failed to send message: " + throwable.getMessage());
    }

    private void handleSuccess(SendResult<String, EmailRequest> result) {
        log.info("Kafka message sent successfully to topic: " + result.getRecordMetadata().topic());
        log.info("Partition: " + result.getRecordMetadata().partition());
        log.info("Offset: " + result.getRecordMetadata().offset());
    }

    private String readEmailTemplate(String classPath) throws IOException {
        File file = ResourceUtils.getFile("classpath:email/" + classPath);
        return FileCopyUtils.copyToString(new FileReader(file));
    }
}
