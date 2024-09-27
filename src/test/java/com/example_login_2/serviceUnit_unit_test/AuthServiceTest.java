package com.example_login_2.serviceUnit_unit_test;

import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.JwtToken;
import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.AuthRepository;
import com.example_login_2.service.AuthServiceImp;
import com.example_login_2.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository repository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImp serviceImp;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.encodedPassword);
    }

    @Test
    public void testCreateUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(TestData.email);
        request.setPassword(TestData.password);

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(TestData.encodedPassword);
        when(repository.save(any(User.class))).thenReturn(mockUser);

        User user = serviceImp.createUser(request);

        assertNotNull(user);
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(TestData.encodedPassword, user.getPassword());

        verify(passwordEncoder).encode(request.getPassword());
        verify(repository).save(any(User.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(TestData.email);

        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> serviceImp.createUser(request));

        verify(repository).existsByEmail(anyString());
    }

    @Test
    public void testUpdateUserRequest() {
        UpdateRequest request = new UpdateRequest();

        when(repository.save(any(User.class))).thenReturn(mockUser);

        User user = serviceImp.updateUserRequest(mockUser, request);

        assertNotNull(user);
        assertEquals(mockUser.getEmail(), user.getEmail());

        verify(repository).save(any(User.class));
    }

    @Test
    public void testRemoveJwtToken() {
        JwtToken activeToken = new JwtToken()
                .setRevoked(false);
        JwtToken revokedToken = new JwtToken()
                .setRevoked(true);
        List<JwtToken> jwtTokens = new ArrayList<>(Arrays.asList(activeToken, revokedToken));

        User user = new User()
                .setJwtToken(jwtTokens);

        serviceImp.deleteJwtIsRevoked(user);

        assertEquals(1, user.getJwtToken().size());
        assertFalse(user.getJwtToken().get(0).isRevoked());

        verify(repository).save(any(User.class));
    }

    @Test
    public void testUpdateEmailConfirm() {
        EmailConfirm emailConfirm = new EmailConfirm()
                .setToken(TestData.tokenEmailConfirm);

        when(repository.save(any(User.class))).thenReturn(mockUser);

        User user = serviceImp.updateEmailConfirm(mockUser, emailConfirm);

        assertNotNull(user);
        assertEquals(emailConfirm.getToken(), user.getEmailConfirm().getToken());

        verify(repository).save(any(User.class));

    }

    @Test
    public void testUpdatePasswordResetToken() {
        String mockToken = TestData.token;
        Instant mockExpireAt = Instant.now().plus(Duration.ofMinutes(15));

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::generateToken).thenReturn(mockToken);

            PasswordResetToken passwordResetToken = new PasswordResetToken()
                    .setToken(mockToken)
                    .setExpiresAt(mockExpireAt);
            mockUser.setPasswordResetToken(passwordResetToken);

            when(repository.save(any(User.class))).thenReturn(mockUser);

            User user = serviceImp.updatePasswordResetToken(mockUser);

            assertNotNull(user);
            assertNotNull(user.getPasswordResetToken());
            assertNotNull(user.getPasswordResetToken().getToken());
            assertNotNull(user.getPasswordResetToken().getExpiresAt());
            assertEquals(mockToken, user.getPasswordResetToken().getToken());
            //เปรียบเทียบค่า Instant โดยไม่สนใจค่าที่แตกต่างกันในระดับนาโนวินาที
            assertEquals(mockExpireAt.getEpochSecond(), user.getPasswordResetToken().getExpiresAt().getEpochSecond());

            verify(repository).save(any(User.class));
        }
    }

    @Test
    public void testUpdateNewPassword() {
        String mockNewPassword = "mock new password";

        when(passwordEncoder.encode(mockNewPassword)).thenReturn(TestData.encodedPassword);
        when(repository.save(any(User.class))).thenReturn(mockUser);

        serviceImp.updateNewPassword(mockUser, mockNewPassword);

        assertNotEquals(mockUser.getPassword(), mockNewPassword);
        assertNull(mockUser.getPasswordResetToken());

        verify(repository).save(any(User.class));
    }

    @Test
    public void testGetUserByEmail() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        User user = serviceImp.getUserByEmail(TestData.email).orElse(null);

        assertNotNull(user);

        verify(repository).findByEmail(anyString());
    }

    @Test
    public void testMatchPassword() {
        String rawPassword = TestData.password;
        String encodedPassword = TestData.encodedPassword;

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean result = serviceImp.matchPassword(rawPassword, encodedPassword);

        assertTrue(result);

        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    public void testGetByPasswordResetToken_Token() {
        when(repository.findByPasswordResetToken_Token(anyString())).thenReturn(Optional.of(mockUser));

        User user = serviceImp.getByPasswordResetToken_Token(TestData.token).orElse(null);

        assertNotNull(user);

        verify(repository).findByPasswordResetToken_Token(anyString());
    }

    @Test
    public void testDeleteUser() {
        serviceImp.deleteUser(TestData.id);

        verify(repository).deleteById(anyLong());
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";

        String password = "password";
        String encodedPassword = "encodedPassword";
        String fileName = "fileName";
        String tokenEmailConfirm = "token email confirm";
        String address = "15/7";
        String token = "token";
    }
}
