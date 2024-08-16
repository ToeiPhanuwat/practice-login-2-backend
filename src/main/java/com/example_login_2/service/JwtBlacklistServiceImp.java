package com.example_login_2.service;

import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;
import com.example_login_2.repository.JwtBlacklistRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

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

    @Override
    public void saveToBlacklist(JwtToken jwtToken) {
        JwtBlacklist newJwtBlacklist = new JwtBlacklist()
                .setToken(jwtToken.getJwtToken())
                .setUserId(jwtToken.getUser().getId())
                .setRevokedAt(Instant.now())
                .setExpiresAt(jwtToken.getExpiresAt());
        jwtBlacklistRepository.save(newJwtBlacklist);
    }

}
