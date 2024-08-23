package com.example_login_2.ServiceUnitTest;

import com.example_login_2.exception.ConflictException;
import com.example_login_2.model.JwtBlacklist;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.JwtBlacklistRepository;
import com.example_login_2.service.JwtBlacklistServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtBlacklistServiceTest {

    @Mock
    private JwtBlacklistRepository repository;

    @InjectMocks
    private JwtBlacklistServiceImp serviceImp;

    private JwtBlacklist mockJwtBlacklist;
    private JwtToken mockJwtToken;

    @BeforeEach
    public void setUp() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("password");

        mockJwtBlacklist = new JwtBlacklist();
        mockJwtBlacklist.setId(TestData.id);
        mockJwtBlacklist.setUserId(TestData.userId);
        mockJwtBlacklist.setToken(TestData.token);
        mockJwtBlacklist.setRevokedAt(TestData.revokedAt);
        mockJwtBlacklist.setExpiresAt(TestData.expireAt);
        mockJwtBlacklist.setAction(TestData.action);

        mockJwtToken = new JwtToken();
        mockJwtToken.setId(TestData.id);
        mockJwtToken.setUser(mockUser);
        mockJwtToken.setJwtToken(TestData.token);
        mockJwtToken.setIssuedAt(TestData.revokedAt);
        mockJwtToken.setExpiresAt(TestData.expireAt);
        mockJwtToken.setRevoked(true);
    }

    @Test
    public void testGetJwtBlacklist() {
        when(repository.findByToken(anyString())).thenReturn(Optional.of(mockJwtBlacklist));

        JwtBlacklist jwtBlacklist = serviceImp.getJwtBlacklist(TestData.token).orElse(null);

        assertNotNull(jwtBlacklist);
        assertEquals(mockJwtBlacklist.getToken(), jwtBlacklist.getToken());

        verify(repository).findByToken(anyString());
    }

    @Test
    public void testSaveToBlacklist_Success() {
        when(repository.findByToken(anyString())).thenReturn(Optional.empty());

        serviceImp.saveToBlacklist(mockJwtToken, TestData.action);

        verify(repository).save(any(JwtBlacklist.class));
    }

    @Test
    public void testSaveToBlacklist_TokenAlreadyExists() {
        when(repository.findByToken(anyString())).thenReturn(Optional.of(mockJwtBlacklist));

        assertThrows(ConflictException.class, () -> serviceImp.saveToBlacklist(mockJwtToken, TestData.action));

        verify(repository, never()).save(any(JwtBlacklist.class));
    }

    interface TestData {
        Long id = 1L;
        String token = "TokenBlacklist";
        Long userId = 1L;
        Instant revokedAt = Instant.now();
        Instant expireAt = Instant.now().plus(Duration.ofMinutes(30));
        String action = "testLogout";
    }
}
