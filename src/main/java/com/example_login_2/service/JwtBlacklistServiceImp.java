package com.example_login_2.service;

import com.example_login_2.exception.ConflictException;
import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;
import com.example_login_2.repository.JwtBlacklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class JwtBlacklistServiceImp implements JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    public JwtBlacklistServiceImp(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    @Override
    public Optional<JwtBlacklist> getJwtBlacklist(String token) {
        return jwtBlacklistRepository.findByToken(token);
    }

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
        jwtBlacklistRepository.save(newJwtBlacklist);
    }

}
