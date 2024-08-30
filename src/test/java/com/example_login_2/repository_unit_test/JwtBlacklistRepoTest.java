package com.example_login_2.repository_unit_test;

import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.repository.JwtBlacklistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class JwtBlacklistRepoTest {

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Test
    public void testFindByToken_Found() {
        JwtBlacklist jwtBlacklist = new JwtBlacklist()
                .setToken(TestData.token)
                .setUserId(TestData.userId)
                .setRevokedAt(TestData.revokedAt)
                .setExpiresAt(TestData.expiresAt)
                .setAction(TestData.action);
        JwtBlacklist savedJwtBlacklist = jwtBlacklistRepository.save(jwtBlacklist);

        JwtBlacklist result = jwtBlacklistRepository.findByToken(savedJwtBlacklist.getToken()).orElse(null);

        assertNotNull(result);
        assertEquals(savedJwtBlacklist.getToken(), result.getToken());
    }

    @Test
    public void testFindByToken_NotFound() {
        JwtBlacklist result = jwtBlacklistRepository.findByToken(TestData.token).orElse(null);

        assertNull(result);
    }

    interface TestData {
        String token = "testToken";
        Long userId = 1L;
        Instant revokedAt = Instant.now();
        Instant expiresAt = Instant.now();
        String action = "logout";
    }
}
