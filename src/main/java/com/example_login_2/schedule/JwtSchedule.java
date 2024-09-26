package com.example_login_2.schedule;

import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.service.AuthService;
import com.example_login_2.service.JwtBlacklistService;
import com.example_login_2.service.JwtTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
@Transactional
public class JwtSchedule {

    private final JwtTokenService jwtTokenService;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    public JwtSchedule(JwtTokenService jwtTokenService, JwtBlacklistService jwtBlacklistService, AuthService authService) {
        this.jwtTokenService = jwtTokenService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.authService = authService;
    }

    @Scheduled(cron = "0 0 14 * * *") //ทุกวัน เวลา 14:00น.
    public void expiredJwtTokenList() {
        List<JwtToken> jwtTokens = jwtTokenService.getJwtTokenExpire(Instant.now());
        log.info("The JWT Token List has expired.");
        for (JwtToken jwtToken : jwtTokens) {
            deleteTokenExpired(jwtToken);
        }
    }

    public void deleteTokenExpired(JwtToken jwtToken) {
        if (jwtToken == null) {
            log.warn("JWT Token is. null.");
            return;
        }

        User user = jwtToken.getUser();
        if (user == null) {
            log.info("No users related to JWT ID: " + jwtToken.getId());
        } else {
            authService.deleteJwtExpired(user, jwtToken);
            log.info("Deleted token (ID: " + jwtToken.getId() + ") successfully for user ID: " + user.getId());
        }
    }


    @Scheduled(cron = "0 13 16 * * *") //ทุกวัน เวลา 15:00น.
    public void expiredJwtBlackList() {
        List<JwtBlacklist> jwtBlacklists = jwtBlacklistService.getJwtBlacklistExpire(Instant.now());

        if (jwtBlacklists.isEmpty()) {
            log.info("No expired JWT tokens found in the blacklist.");
        } else {
            log.info("The following JWT tokens have expired and will be removed:");
            for (JwtBlacklist jwtBlacklist : jwtBlacklists) {
                log.info("Removing expired token ID: " + jwtBlacklist.getId());
                deleteTokenInBlacklist(jwtBlacklist);
            }
        }
    }

    public void deleteTokenInBlacklist(JwtBlacklist jwtBlacklist) {
        jwtBlacklistService.delete(jwtBlacklist);
        log.info("Deleted token (ID: " + jwtBlacklist.getId() + ") successfully.");
    }
}
