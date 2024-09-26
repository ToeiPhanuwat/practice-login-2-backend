package com.example_login_2.service;

import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JwtBlacklistService {

    void saveToBlacklist(JwtToken jwtToken, String action);

    Optional<JwtBlacklist> getJwtBlacklist(String token);

    List<JwtBlacklist> getJwtBlacklistExpire(Instant currentTime);

    void delete(JwtBlacklist jwtBlacklist);
}
