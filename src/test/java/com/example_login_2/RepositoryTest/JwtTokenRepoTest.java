package com.example_login_2.RepositoryTest;

import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.AuthRepository;
import com.example_login_2.repository.JwtTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class JwtTokenRepoTest {

    @Autowired
    private JwtTokenRepository jwtTokenRepository;
    @Autowired
    private AuthRepository authRepository;

    @Test
    public void testFindByJwtToken_Found() {
        User user = new User()
                .setEmail("test@example.com")
                .setPassword("password");
        User savedUser = authRepository.save(user);

        JwtToken jwtToken = new JwtToken()
                .setUser(user)
                .setJwtToken(TestData.token)
                .setIssuedAt(TestData.now)
                .setExpiresAt(TestData.expiresAt)
                .setRevoked(TestData.isRevoked);
        JwtToken savedJwtToken = jwtTokenRepository.save(jwtToken);

        JwtToken result = jwtTokenRepository.findByJwtToken(savedJwtToken.getJwtToken()).orElse(null);

        assertNotNull(result);
        assertEquals(TestData.token, result.getJwtToken());
    }

    @Test
    public void testFindByJwtToken_NotFound() {
        String token = "nonExistentToken";

        JwtToken result = jwtTokenRepository.findByJwtToken(token).orElse(null);

        assertNull(result);
    }

    @Test
    public void testFindUserByJwtToken_Found() {
        User user = new User()
                .setEmail("test@example.com")
                .setPassword("password");
        User savedUser = authRepository.save(user);

        JwtToken jwtToken = new JwtToken()
                .setUser(user)
                .setJwtToken(TestData.token)
                .setIssuedAt(TestData.now)
                .setExpiresAt(TestData.expiresAt)
                .setRevoked(TestData.isRevoked);
        JwtToken savedJwtToken = jwtTokenRepository.save(jwtToken);

        User result = jwtTokenRepository.findUserByJwtToken(savedJwtToken.getJwtToken()).orElse(null);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
    }

    @Test
    public void testFindUserByJwtToken_NotFound() {
        String token = "nonExistentToken";

        User result = jwtTokenRepository.findUserByJwtToken(token).orElse(null);

        assertNull(result);
    }

    interface TestData {
        String token = "testToken";
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(1));
        boolean isRevoked = false;
    }
}
