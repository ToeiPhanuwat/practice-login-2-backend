package com.example_login_2.service;

import com.example_login_2.exception.ConflictException;
import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;
import com.example_login_2.repository.JwtBlacklistRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class JwtBlacklistServiceImp implements JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    public JwtBlacklistServiceImp(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    @Override
    public Optional<JwtBlacklist> getJwtBlacklist(String token) {
        return jwtBlacklistRepository.findByToken(token);
    }

    @Transactional
    @Override
    public void saveToBlacklist(JwtToken jwtToken, String action) {
        boolean existing = getJwtBlacklist(jwtToken.getJwtToken()).isPresent();
        if (existing) throw ConflictException.handleJwtTokenDuplicate();

        JwtBlacklist newJwtBlacklist = new JwtBlacklist()
                .setToken(jwtToken.getJwtToken())
                .setUserId(jwtToken.getUser().getId())
                .setRevokedAt(Instant.now())
                .setExpiresAt(jwtToken.getExpiresAt())
                .setAction(action);
        log.info("JWT token revocation completed successfully.");
        jwtBlacklistRepository.save(newJwtBlacklist);
    }

    @Override
    public List<JwtBlacklist> getJwtBlacklistExpire(Instant currentTime) {
        return jwtBlacklistRepository.findExpiredTokens(currentTime);
    }

    @Transactional
    @Override
    public void delete(JwtBlacklist jwtBlacklist) {
        jwtBlacklistRepository.delete(jwtBlacklist);
        log.info("Deleted expired token (ID: {}) from blacklist.", jwtBlacklist.getId());
    }
}
