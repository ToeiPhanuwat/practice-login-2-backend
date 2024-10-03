package com.example_login_2.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JwtTokenService {

    JwtToken firstCreate(User user);

    JwtToken doGenerateJwtToken(User user, String jwt, Instant now, Instant expireAt);

    String tokenize(User user, Instant now, Instant expireAt);

    DecodedJWT verify(String token);

    Optional<JwtToken> getJwtToken(String token);

    Optional<User> getUserByToken(String token);

    JwtToken getCurrentToken();

    User getCurrentUserByToken();

    JwtToken generateJwtToken(User user);

    void validateJwtToken();

    JwtToken validateToken(String token);

    void revokedToken(JwtToken jwtToken);

    List<JwtToken> getJwtTokenExpire(Instant currentTime);
}
