package com.example_login_2.service;

import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;

import java.util.Optional;

public interface JwtBlacklistService {

    void saveToBlacklist(JwtToken jwtToken);

    Optional<JwtBlacklist> getJwtBlacklist(String token);
}
