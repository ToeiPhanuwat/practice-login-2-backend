package com.example_login_2.business_unit_test;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.business.EmailBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.*;
import com.example_login_2.model.*;
import com.example_login_2.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthBusinessTest {
    @Mock
    private AuthService authService;
    @Mock
    private EmailConfirmService emailConfirmService;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private StorageService storageService;
    @Mock
    private AddressService addressService;
    @Mock
    private JwtBlacklistService jwtBlacklistService;
    @Mock
    private EmailBusiness emailBusiness;
    @InjectMocks
    private AuthBusiness business;

    private User mockUser;
    private EmailConfirm mockEmailConfirm;
    private Address mockAddress;
    private JwtToken mockJwt;
    private LoginRequest mockLoginRequest;
    private ActivateRequest mockActivateRequest;
    private String mockToken;
    private PasswordResetRequest mockPasswordResetRequest;
    private PasswordResetToken mockPasswordResetToken;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.encodedPassword);
        mockUser.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));

        mockEmailConfirm = new EmailConfirm();
        mockEmailConfirm.setToken(TestData.tokenEmailConfirm);

        mockAddress = new Address();

        mockJwt = new JwtToken();
        mockJwt.setJwtToken(TestData.tokenJwt);

        mockUser.setEmailConfirm(mockEmailConfirm);
        mockUser.setAddress(mockAddress);

        mockLoginRequest = new LoginRequest();
        mockLoginRequest.setEmail(TestData.email);
        mockLoginRequest.setPassword(TestData.password);

        mockActivateRequest = new ActivateRequest();
        mockActivateRequest.setToken(TestData.tokenEmailConfirm);

        mockToken = TestData.tokenEmailConfirm;

        mockPasswordResetRequest = new PasswordResetRequest();
        mockPasswordResetRequest.setToken("Token reset password");
        mockPasswordResetRequest.setNewPassword(TestData.password);

        mockPasswordResetToken = new PasswordResetToken();
        mockPasswordResetToken.setToken("Password reset token");
    }

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        mockEmailConfirm.setActivated(false);

        when(authService.createUser(any(RegisterRequest.class))).thenReturn(mockUser);
        when(emailConfirmService.createEmailConfirm(any(User.class))).thenReturn(mockEmailConfirm);
        when(authService.updateEmailConfirm(any(User.class), any(EmailConfirm.class))).thenReturn(mockUser);
        when(addressService.createAddress(any(User.class))).thenReturn(mockAddress);
        when(authService.updateAddress(any(User.class), any(Address.class))).thenReturn(mockUser);

        ApiResponse<ModelDTO> response = business.register(request);

        assertTrue(response.isSuccess());
        assertEquals(TestData.email, response.getData().getEmail());
        assertEquals("false", response.getData().getActivated());
        assertEquals(TestData.tokenEmailConfirm, response.getData().getActivationToken());

        verify(authService).createUser(any(RegisterRequest.class));
        verify(emailConfirmService).createEmailConfirm(any(User.class));
        verify(authService).updateEmailConfirm(any(User.class), any(EmailConfirm.class));
        verify(addressService).createAddress(any(User.class));
        verify(authService).updateAddress(any(User.class), any(Address.class));
    }

    @Test
    public void testLogin_Success() {
        mockEmailConfirm.setActivated(true);

        when(authService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(authService.matchPassword(anyString(), anyString())).thenReturn(true);
        when(emailConfirmService.getEmailConfirmByUserId(anyLong())).thenReturn(Optional.of(mockEmailConfirm));
        when(jwtTokenService.generateJwtToken(any(User.class))).thenReturn(mockJwt);

        ApiResponse<ModelDTO> response = business.login(mockLoginRequest);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getJwtToken());

        verify(authService).getUserByEmail(anyString());
        verify(authService).matchPassword(anyString(), anyString());
        verify(emailConfirmService).getEmailConfirmByUserId(anyLong());
        verify(jwtTokenService).generateJwtToken(any(User.class));
    }

    @Test
    public void testLogin_UserNotFound() {
        when(authService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.login(mockLoginRequest));

        verify(authService).getUserByEmail(anyString());
    }

    @Test
    public void testLogin_MatchPasswordFailed() {
        when(authService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(authService.matchPassword(anyString(), anyString())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> business.login(mockLoginRequest));

        verify(authService).getUserByEmail(anyString());
        verify(authService).matchPassword(anyString(), anyString());
    }

    @Test
    public void testLogin_EmailConfirmNotFound() {
        when(authService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(authService.matchPassword(anyString(), anyString())).thenReturn(true);
        when(emailConfirmService.getEmailConfirmByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.login(mockLoginRequest));

        verify(authService).getUserByEmail(anyString());
        verify(authService).matchPassword(anyString(), anyString());
        verify(emailConfirmService).getEmailConfirmByUserId(anyLong());
    }

    @Test
    public void testLogin_NotYetActivatedEmail() {
        mockEmailConfirm.setActivated(false);

        when(authService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(authService.matchPassword(anyString(), anyString())).thenReturn(true);
        when(emailConfirmService.getEmailConfirmByUserId(anyLong())).thenReturn(Optional.of(mockEmailConfirm));

        assertThrows(ForbiddenException.class, () -> business.login(mockLoginRequest));

        verify(authService).getUserByEmail(anyString());
        verify(authService).matchPassword(anyString(), anyString());
        verify(emailConfirmService).getEmailConfirmByUserId(anyLong());
    }

    @Test
    public void testActivate_Success() {
        Date expireAt = new Date(System.currentTimeMillis() + 10000);

        EmailConfirm emailConfirm = new EmailConfirm()
                .setToken(TestData.tokenEmailConfirm)
                .setActivated(false)
                .setExpiresAt(expireAt);

        mockEmailConfirm.setExpiresAt(expireAt);
        mockEmailConfirm.setActivated(true);

        when(emailConfirmService.getEmailConfirmByToken(anyString())).thenReturn(Optional.of(emailConfirm));
        when(emailConfirmService.updateEnableVerificationEmail(any(EmailConfirm.class))).thenReturn(mockEmailConfirm);

        ApiResponse<ModelDTO> response = business.activate(mockToken);

        assertTrue(response.isSuccess());
        assertEquals("true", response.getData().getActivated());

        verify(emailConfirmService).getEmailConfirmByToken(anyString());
        verify(emailConfirmService).updateEnableVerificationEmail(any(EmailConfirm.class));
    }

    @Test
    public void testActivate_TokenNotFound() {
        when(emailConfirmService.getEmailConfirmByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.activate(mockToken));

        verify(emailConfirmService).getEmailConfirmByToken(anyString());
    }

    @Test
    public void testActivate_ActivateAlready() {
        mockEmailConfirm.setActivated(true);
        when(emailConfirmService.getEmailConfirmByToken(anyString())).thenReturn(Optional.of(mockEmailConfirm));

        assertThrows(ConflictException.class, () -> business.activate(mockToken));

        verify(emailConfirmService).getEmailConfirmByToken(anyString());
    }

    @Test
    public void testActivate_TokenExpired() {
        Date expire = new Date(System.currentTimeMillis() - 10000);
        mockEmailConfirm.setExpiresAt(expire);
        mockEmailConfirm.setActivated(false);

        when(emailConfirmService.getEmailConfirmByToken(anyString())).thenReturn(Optional.of(mockEmailConfirm));

        assertThrows(GoneException.class, () -> business.activate(mockToken));

        verify(emailConfirmService).getEmailConfirmByToken(anyString());
    }

    @Test
    public void testLogout_UserNotFound() {
        when(jwtTokenService.getCurrentToken()).thenReturn(mockJwt);
        doNothing().when(jwtTokenService).revokedToken(any(JwtToken.class));
        doNothing().when(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());

        assertThrows(NotFoundException.class, () -> business.logout());

        verify(jwtTokenService).getCurrentToken();
        verify(jwtTokenService).revokedToken(any(JwtToken.class));
        verify(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
    }

    @Test
    public void testLogout_Success() {
        mockJwt.setUser(mockUser);

        when(jwtTokenService.getCurrentToken()).thenReturn(mockJwt);
        doNothing().when(jwtTokenService).revokedToken(any(JwtToken.class));
        doNothing().when(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
        doNothing().when(authService).removeJwtToken(any(User.class));

        ApiResponse<String> response = business.logout();

        assertTrue(response.isSuccess());

        verify(jwtTokenService).getCurrentToken();
        verify(jwtTokenService).revokedToken(any(JwtToken.class));
        verify(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
        verify(authService).removeJwtToken(any(User.class));
    }

    @Test
    public void testResentActivationEmail_Success() {
        mockEmailConfirm.setActivated(false);
        when(emailConfirmService.getEmailConfirmByToken(anyString())).thenReturn(Optional.of(mockEmailConfirm));
        when(emailConfirmService.updateEmailConfirm(any(EmailConfirm.class))).thenReturn(mockEmailConfirm);

        ApiResponse<String> response = business.resendActivationEmail(TestData.tokenJwt);

        assertTrue(response.isSuccess());

        verify(emailConfirmService).updateEmailConfirm(any(EmailConfirm.class));
    }

    @Test
    public void testForgotPassword_UserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(TestData.email);

        when(authService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.forgotPassword(request));

        verify(authService).getUserByEmail(anyString());
    }

    @Test
    public void testForgotPassword_Success() {
        mockPasswordResetToken.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));
        mockUser.setPasswordResetToken(mockPasswordResetToken);

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(TestData.email);

        when(authService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(authService.updatePasswordResetToken(any(User.class))).thenReturn(mockUser);

        ApiResponse<ModelDTO> response = business.forgotPassword(request);

        assertTrue(response.isSuccess());

        verify(authService).getUserByEmail(anyString());
        verify(authService).updatePasswordResetToken(any(User.class));
    }

    @Test
    public void testResetPassword_TokenNotFound() {
        ;
        when(authService.getByPasswordResetToken_Token(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.resetPassword(mockPasswordResetRequest));

        verify(authService).getByPasswordResetToken_Token(anyString());
    }

    @Test
    public void testResetPassword_TokenExpire() {
        mockPasswordResetToken.setExpiresAt(Instant.now().plus(Duration.ofMinutes(-15)));
        mockUser.setPasswordResetToken(mockPasswordResetToken);

        when(authService.getByPasswordResetToken_Token(anyString())).thenReturn(Optional.of(mockUser));

        assertThrows(GoneException.class, () -> business.resetPassword(mockPasswordResetRequest));

        verify(authService).getByPasswordResetToken_Token(anyString());
    }

    @Test
    public void testResetPassword_Success() {
        mockPasswordResetToken.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));
        mockUser.setPasswordResetToken(mockPasswordResetToken);

        when(authService.getByPasswordResetToken_Token(anyString())).thenReturn(Optional.of(mockUser));
        doNothing().when(authService).updateNewPassword(any(User.class), anyString());

        ApiResponse<String> response = business.resetPassword(mockPasswordResetRequest);

        assertTrue(response.isSuccess());

        verify(authService).getByPasswordResetToken_Token(anyString());
        verify(authService).updateNewPassword(any(User.class), anyString());
    }

    @Test
    public void testRefreshJwtToken_UserNotFound() {
        when(jwtTokenService.getCurrentToken()).thenReturn(mockJwt);
        doNothing().when(jwtTokenService).revokedToken(any(JwtToken.class));
        doNothing().when(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());

        assertThrows(NotFoundException.class, () -> business.refreshJwtToken());

        verify(jwtTokenService).getCurrentToken();
        verify(jwtTokenService).revokedToken(any(JwtToken.class));
        verify(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
    }

    @Test
    public void testRefreshJwtToken_Success() {
        mockJwt.setUser(mockUser);

        when(jwtTokenService.getCurrentToken()).thenReturn(mockJwt);
        doNothing().when(jwtTokenService).revokedToken(any(JwtToken.class));
        doNothing().when(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
        doNothing().when(authService).removeJwtToken(any(User.class));
        when(jwtTokenService.generateJwtToken(any(User.class))).thenReturn(mockJwt);

        ApiResponse<ModelDTO> response = business.refreshJwtToken();

        assertTrue(response.isSuccess());

        verify(jwtTokenService).getCurrentToken();
        verify(jwtTokenService).revokedToken(any(JwtToken.class));
        verify(jwtBlacklistService).saveToBlacklist(any(JwtToken.class), anyString());
        verify(authService).removeJwtToken(any(User.class));
        verify(jwtTokenService).generateJwtToken(any(User.class));
    }

    @Test
    public void testGetUserById_Success() {
        mockUser.setAddress(mockAddress);
        mockEmailConfirm.setActivated(true);
        mockUser.setEmailConfirm(mockEmailConfirm);

        when(jwtTokenService.getCurrentUserByToken()).thenReturn(mockUser);

        ApiResponse<ModelDTO> response = business.getUserById();

        assertTrue(response.isSuccess());

        verify(jwtTokenService).getCurrentUserByToken();
    }

    @Test
    public void testUpdateUser_Success() {
        UpdateRequest request = new UpdateRequest();
        MultipartFile file = mock(MultipartFile.class);
        String fileName = TestData.fileName;

        when(jwtTokenService.getCurrentUserByToken()).thenReturn(mockUser);
        when(storageService.uploadProfilePicture(any(MultipartFile.class))).thenReturn(fileName);
        when(authService.updateUserRequest(any(User.class), any(UpdateRequest.class))).thenReturn(mockUser);
        when(addressService.updateAddress(any(User.class), any(UpdateRequest.class))).thenReturn(mockAddress);
        when(authService.updateAddress(any(User.class), any(Address.class))).thenReturn(mockUser);

        ApiResponse<ModelDTO> response = business.updateUser(file, request);

        assertTrue(response.isSuccess());

        verify(jwtTokenService).getCurrentUserByToken();
        verify(storageService).uploadProfilePicture(any(MultipartFile.class));
        verify(authService).updateUserRequest(any(User.class), any(UpdateRequest.class));
        verify(addressService).updateAddress(any(User.class), any(UpdateRequest.class));
        verify(authService).updateAddress(any(User.class), any(Address.class));
    }

    @Test
    public void testDeleteUser() {
        when(jwtTokenService.getCurrentUserByToken()).thenReturn(mockUser);
        doNothing().when(authService).deleteUser(anyLong());

        business.deleteUser();

        verify(jwtTokenService).getCurrentUserByToken();
        verify(authService).deleteUser(anyLong());
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";

        String password = "password";
        String encodedPassword = "encodedPassword";
        String fileName = "fileName";
        String tokenEmailConfirm = "token email confirm";
        String tokenJwt = "token jwt";
    }
}
