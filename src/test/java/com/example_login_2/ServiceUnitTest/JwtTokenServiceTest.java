package com.example_login_2.ServiceUnitTest;

import com.example_login_2.exception.UnauthorizedException;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.JwtTokenRepository;
import com.example_login_2.service.JwtTokenServiceImp;
import com.example_login_2.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    @Mock
    private JwtTokenRepository repository;

    @Spy
    @InjectMocks
    private JwtTokenServiceImp serviceImp;

    private User mockUser;
    private JwtToken mockJwtToken;
    private String mockToken;
    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.password);

        mockJwtToken = new JwtToken();
        mockJwtToken.setId(TestData.id);
        mockJwtToken.setUser(mockUser);
        mockJwtToken.setJwtToken(TestData.mockToken);
        mockJwtToken.setIssuedAt(TestData.now);
        mockJwtToken.setExpiresAt(TestData.expireAt);
        mockJwtToken.setRevoked(TestData.isRevokedFalse);

        mockToken = TestData.mockToken;

    }

    @Test
    public void testDoGenerateJwtToken() {
        when(repository.save(any(JwtToken.class))).thenReturn(mockJwtToken);

        JwtToken jwtToken = serviceImp.doGenerateJwtToken(mockUser, mockToken, TestData.now, TestData.expireAt);

        assertNotNull(jwtToken);
        assertEquals(mockUser, jwtToken.getUser());
        assertEquals(mockToken, jwtToken.getJwtToken());
        assertEquals(TestData.now, jwtToken.getIssuedAt());
        assertEquals(TestData.expireAt, jwtToken.getExpiresAt());

        verify(repository).save(any(JwtToken.class));
    }

    @Test // ถ้าจะ test อันนี้ ต้องใช้ @Spy ร่วมกบ @InjectMocks
    public void testGenerateJwtToken() {
        when(repository.save(any(JwtToken.class))).thenReturn(mockJwtToken);
        doReturn(mockToken).when(serviceImp).tokenize(any(User.class), any(Instant.class), any(Instant.class));

        JwtToken jwtToken = serviceImp.generateJwtToken(mockUser);

        assertNotNull(jwtToken);
        assertEquals(mockJwtToken, jwtToken);
        assertEquals(mockUser, jwtToken.getUser());
        assertEquals(mockToken, jwtToken.getJwtToken());

        verify(serviceImp).tokenize(eq(mockUser), any(Instant.class), any(Instant.class));
        verify(serviceImp).doGenerateJwtToken(eq(mockUser), eq(mockToken), any(Instant.class), any(Instant.class));
    }

    @Test
    public void testRevokedToken() {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setRevoked(TestData.isRevokedTrue);

        serviceImp.revokedToken(jwtToken);

        assertTrue(jwtToken.isRevoked());
        verify(repository).save(jwtToken);
    }

    @Test
    public void testGetCurrentToken_Success() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentToken).thenReturn((Optional.of(mockToken)));
            when(repository.findByJwtToken(mockToken)).thenReturn(Optional.of(mockJwtToken));

            JwtToken jwtToken = serviceImp.getCurrentToken();

            assertNotNull(jwtToken);
            assertEquals(mockToken, jwtToken.getJwtToken());

            verify(repository).findByJwtToken(mockToken);
        }
    }
    @Test
    public void testGetCurrentToken_TokenNotFound() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentToken).thenReturn(Optional.of(mockToken));
            when(repository.findByJwtToken(mockToken)).thenReturn(Optional.empty());

            assertThrows(UnauthorizedException.class, () -> serviceImp.getCurrentToken());

            verify(repository).findByJwtToken(mockToken);
        }
    }

    @Test
    public void testGetCurrentToken_NoTokenPresent() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentToken).thenReturn(Optional.empty());

            assertThrows(UnauthorizedException.class, () -> serviceImp.getCurrentToken());

            verify(repository, never()).findByJwtToken(anyString());
        }
    }

    @Test
    public void testGetCurrentUserByToken_Success() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentToken).thenReturn(Optional.of(mockToken));
            when(repository.findUserByJwtToken(mockToken)).thenReturn(Optional.of(mockUser));

            User user = serviceImp.getCurrentUserByToken();

            assertNotNull(user);

            verify(repository).findUserByJwtToken(mockToken);
        }
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";
        String password = "password";

        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofDays(1));
        String mockToken = "mockToken";
        boolean isRevokedFalse = false;
        boolean isRevokedTrue = true;
    }
}
