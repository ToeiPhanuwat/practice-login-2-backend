package com.example_login_2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.exception.UnauthorizedException;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.JwtTokenRepository;
import com.example_login_2.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j2
public class JwtTokenServiceImp implements JwtTokenService {

    private final JwtTokenRepository jwtTokenRepository;

    @Value("${spring.token.secret}")
    private String secret;
    @Value("${spring.token.issuer}")
    private String issuer;

    public JwtTokenServiceImp(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    public JwtToken firstCreate(User user) {
        JwtToken jwtToken = new JwtToken().setUser(user);
        return jwtTokenRepository.save(jwtToken);
    }

    @Override
    public JwtToken generateJwtToken(User user) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofDays(1));
        String jwt = tokenize(user, now, expireAt);
        return doGenerateJwtToken(user, jwt, now, expireAt);
    }

    @Override
    public JwtToken doGenerateJwtToken(User user, String jwt, Instant now, Instant expireAt) {
        JwtToken jwtToken = new JwtToken()
                .setUser(user)
                .setJwtToken(jwt)
                .setIssuedAt(now)
                .setExpiresAt(expireAt)
                .setRevoked(false);
        return jwtTokenRepository.save(jwtToken);
    }

    @Override
    public String tokenize(User user, Instant now, Instant expireAt) {
        return JWT
                .create()
                .withIssuer(issuer)
                .withClaim("userId", user.getId())
                .withClaim("roles", new ArrayList<>(user.getRoles()))
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .sign(algorithm());
    }

    @Override
    public DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT
                    .require(algorithm())
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (Exception ex) {
            return null;
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public Optional<JwtToken> getJwtToken(String token) {
        return jwtTokenRepository.findByJwtToken(token);
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return jwtTokenRepository.findUserByJwtToken(token);
    }

    @Override
    public JwtToken validateToken(String token) {
        JwtToken jwtToken = jwtTokenRepository.findByJwtToken(token)
                .orElseThrow(NotFoundException::tokenNotFound);

        if (jwtToken.isRevoked()) throw UnauthorizedException.handleRevokedToken();

        Instant now = Instant.now();
        if (now.isAfter(jwtToken.getExpiresAt())) throw UnauthorizedException.handleExpiredToken();

        return jwtToken;
    }

    @Override
    public void revokedToken(JwtToken jwtToken) {
        jwtToken.setRevoked(true);
        jwtTokenRepository.save(jwtToken);
    }

    @Override
    public JwtToken getCurrentToken() {
        String token = SecurityUtil.getCurrentToken()
                .orElseThrow(UnauthorizedException::unauthorized);
        return jwtTokenRepository.findByJwtToken(token)
                .orElseThrow(UnauthorizedException::handleTokenlNotFound);
    }

    @Override
    public User getCurrentUserByToken() {
        String token = SecurityUtil.getCurrentToken()
                .orElseThrow(UnauthorizedException::unauthorized);
        return jwtTokenRepository.findUserByJwtToken(token)
                .orElseThrow(UnauthorizedException::handleTokenlNotFound);
    }

    @Override
    public void validateJwtToken() {
        String token = SecurityUtil.getCurrentToken()
                .orElseThrow(UnauthorizedException::unauthorized);
        jwtTokenRepository.findUserByJwtToken(token)
                .orElseThrow(UnauthorizedException::handleTokenlNotFound);
    }
}
