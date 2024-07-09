package com.example_login_2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
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
    public JwtToken generateJwtToken(User user) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofDays(1));
        String jwt = tokenize(user, now, expireAt);
        return createOrUpdateJwtToken(user, jwt, now, expireAt);
    }

    @Override
    public JwtToken createOrUpdateJwtToken(User user, String jwt, Instant now, Instant expireAt) {
        JwtToken jwtToken = user.getJwtToken();
        if (jwtToken == null) {
            jwtToken = new JwtToken();
            jwtToken.setUser(user);
        }
        jwtToken
                .setJwtToken(jwt)
                .setIssuedAt(now)
                .setExpiresAt(expireAt);
        return jwtTokenRepository.save(jwtToken);
    }

    @Override
    public String tokenize(User user, Instant now, Instant expireAt) {
        return JWT
                .create()
                .withIssuer(issuer)
                .withClaim("principal", user.getId())
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
        return jwtTokenRepository.findByjwtToken(token);
    }


}
