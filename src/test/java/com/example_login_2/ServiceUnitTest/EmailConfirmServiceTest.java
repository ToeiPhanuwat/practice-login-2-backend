package com.example_login_2.ServiceUnitTest;

import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;
import com.example_login_2.repository.EmailConfirmRepository;
import com.example_login_2.service.EmailConfirmServiceImp;
import com.example_login_2.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmServiceTest {
    @Mock
    private EmailConfirmRepository repository;
    @InjectMocks
    private EmailConfirmServiceImp serviceImp;

    private User mockUser;
    private EmailConfirm mockEmailConfirm;
    @Captor
    private ArgumentCaptor<EmailConfirm> emailConfirmCaptor;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.password);

        mockEmailConfirm = new EmailConfirm();
        mockEmailConfirm.setId(TestData.id);
        mockEmailConfirm.setUser(mockUser);
        mockEmailConfirm.setActivated(false);

        mockUser.setEmailConfirm(mockEmailConfirm);
    }

    @Test
    public void testCreateEmailConfirm() {
        when(repository.save(any(EmailConfirm.class))).thenReturn(mockEmailConfirm);

        EmailConfirm emailConfirm = serviceImp.createEmailConfirm(mockUser);
        verify(repository).save(emailConfirmCaptor.capture());
        EmailConfirm captured = emailConfirmCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getUser());
        assertNotNull(captured.getToken());
        assertNotNull(captured.getExpiresAt());

        assertNull(mockEmailConfirm.getToken());
        assertNull(mockEmailConfirm.getExpiresAt());

        assertEquals(mockUser, captured.getUser());
    }

    @Test
    public void testGetEmailConfirmByUserId() {
        when(repository.findByUserId(anyLong())).thenReturn(Optional.ofNullable(mockEmailConfirm));

        EmailConfirm emailConfirm = serviceImp.getEmailConfirmByUserId(TestData.id).orElse(null);

        assertNotNull(emailConfirm);
        assertEquals(mockUser.getId(), emailConfirm.getUser().getId());
        assertEquals(mockEmailConfirm.getId(), emailConfirm.getId());

        verify(repository).findByUserId(anyLong());
    }

    @Test
    public void testGetEmailConfirmByToken() {
        when(repository.findByToken(anyString())).thenReturn(Optional.ofNullable(mockEmailConfirm));

        EmailConfirm emailConfirm = serviceImp.getEmailConfirmByToken(TestData.token).orElse(null);

        assertNotNull(emailConfirm);
        assertEquals(mockUser.getId(), emailConfirm.getUser().getId());
        assertEquals(mockEmailConfirm.getId(), emailConfirm.getId());
        assertEquals(mockEmailConfirm.getToken(), emailConfirm.getToken());

        verify(repository).findByToken(anyString());
    }

    @Test
    public void testUpdateEnableVerificationEmail() {
        when(repository.save(any(EmailConfirm.class))).thenReturn(mockEmailConfirm);

        EmailConfirm updateEmailConfirm = serviceImp.updateEnableVerificationEmail(mockEmailConfirm);

        assertNotNull(updateEmailConfirm);
        assertEquals(mockEmailConfirm.getId(), updateEmailConfirm.getId());
        assertTrue(updateEmailConfirm.isActivated());

        verify(repository).save(any(EmailConfirm.class));
    }

    @Test
    public void testUpdateEmailConfirm() {
        when(repository.save(any(EmailConfirm.class))).thenReturn(mockEmailConfirm);

        EmailConfirm updateEmailConfirm = serviceImp.updateEmailConfirm(mockEmailConfirm);

        assertNotNull(updateEmailConfirm);
        assertEquals(mockEmailConfirm.getId(), updateEmailConfirm.getId());
        assertEquals(mockEmailConfirm.getToken(), updateEmailConfirm.getToken());
        assertNotNull(updateEmailConfirm.getExpiresAt());
        assertNotNull(updateEmailConfirm.getToken());

        verify(repository).save(any(EmailConfirm.class));
    }

    @Test
    public void testGetUserActivatedFalse() {
        when(repository.findByActivatedFalse()).thenReturn(Collections.singletonList(mockUser));

        List<User> result = serviceImp.getUserActivatedFalse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUser, result.get(0));
        assertFalse(result.get(0).getEmailConfirm().isActivated());
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";
        String password = "password";
        String token = SecurityUtil.generateToken();
    }
}
