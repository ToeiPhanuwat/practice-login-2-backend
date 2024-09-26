package com.example_login_2.schedule;

import com.example_login_2.model.User;
import com.example_login_2.service.AuthService;
import com.example_login_2.service.EmailConfirmService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class UsersCleanup {

    private final AuthService authService;
    private final EmailConfirmService emailConfirmService;

    public UsersCleanup(AuthService authService, EmailConfirmService emailConfirmService) {
        this.authService = authService;
        this.emailConfirmService = emailConfirmService;
    }

    //parameter @Scheduled(cron = "")
    // 1 วินาที 0-59
    // 2 นาที 0-59
    // 3 ชม. 0-23
    // 4 วัน 1-31
    // 5 เดือน 1-12
    // 6 วันในสัปดาห์ 0-6 หรือ Sun–Sat

    @Scheduled(cron = "0 0 15 * * *") //ทุกวัน เวลา 15:00น.
    private void clearTheDataOfInactiveUsers() {
        List<User> users = emailConfirmService.getUserActivatedFalse();
        log.info("Removing users who haven't verified their email.");
        for (User user : users) {
            deleteUser(user);
        }
    }

    private void deleteUser(User user) {
        authService.deleteUser(user.getId());
        log.info("Delete user ID : " + user.getId() + " Successful");
    }

}
