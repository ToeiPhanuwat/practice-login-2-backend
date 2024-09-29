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

        if (jwtTokens.isEmpty()) {
            log.info("No expired JWT tokens found.");
        } else {
            log.info("Found {} expired JWT tokens to delete.", jwtTokens.size());
            for (JwtToken jwtToken : jwtTokens) {
                deleteTokenExpired(jwtToken);
            }
        }
    }

    public void deleteTokenExpired(JwtToken jwtToken) {
        if (jwtToken == null) {
            log.warn("JWT token is null, cannot delete.");
            return;
        }

        User user = jwtToken.getUser();
        if (user == null) {
            log.info("No user associated with JWT ID: {}", jwtToken.getId());
        } else {
            authService.deleteJwtExpired(user, jwtToken);
        }
    }


    @Scheduled(cron = "0 0 15 * * *") //ทุกวัน เวลา 15:00น.
    public void expiredJwtBlackList() {
        List<JwtBlacklist> jwtBlacklists = jwtBlacklistService.getJwtBlacklistExpire(Instant.now());

        if (jwtBlacklists.isEmpty()) {
            log.info("No expired JWT tokens found in the blacklist.");
        } else {
            log.info("Found {} expired JWT tokens in the blacklist to remove.",jwtBlacklists.size());
            for (JwtBlacklist jwtBlacklist : jwtBlacklists) {
                deleteTokenInBlacklist(jwtBlacklist);
            }
        }
    }

    public void deleteTokenInBlacklist(JwtBlacklist jwtBlacklist) {
        jwtBlacklistService.delete(jwtBlacklist);
    }
}
