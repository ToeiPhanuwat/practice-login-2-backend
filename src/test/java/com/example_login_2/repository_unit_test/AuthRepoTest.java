package com.example_login_2.repository_unit_test;

import com.example_login_2.model.PasswordResetToken;
import com.example_login_2.model.User;
import com.example_login_2.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AuthRepoTest {

    @Autowired
    private AuthRepository authRepository;

    @Test
    public void testExistsEmail() {
        User user = new User()
                .setFirstName(TestData.firstName)
                .setEmail(TestData.email)
                .setPassword(TestData.password);
        User savedUser = authRepository.save(user);

        boolean existing = authRepository.existsByEmail(savedUser.getEmail());

        assertTrue(existing);
    }

    @Test
    public void testNonExistsEmail() {
        String email = "nonExistentEmail";

        boolean nonExisting = authRepository.existsByEmail(email);

        assertFalse(nonExisting);
    }

    @Test
    public void testFindByEmail_Found() {
        User user = new User()
                .setFirstName(TestData.firstName)
                .setEmail(TestData.email)
                .setPassword(TestData.password);
        User savedUser = authRepository.save(user);

        User result = authRepository.findByEmail(savedUser.getEmail()).orElse(null);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
    }

    @Test
    public void testFindByEmail_NotFound() {
        User user = authRepository.findByEmail(TestData.email).orElse(null);

        assertNull(user);
    }

    @Test
    public void testFindByPasswordResetToken_Token_Found() {
        PasswordResetToken passwordResetToken = new PasswordResetToken()
                .setToken(TestData.passwordResetToken);

        User user = new User()
                .setFirstName(TestData.firstName)
                .setEmail(TestData.email)
                .setPassword(TestData.password)
                .setPasswordResetToken(passwordResetToken);
        User savedUser = authRepository.save(user);

        User result = authRepository.findByPasswordResetToken_Token(TestData.passwordResetToken).orElse(null);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId()
        );
    }

    @Test
    public void testFindByPasswordResetToken_Token_NotFound() {
        User result = authRepository.findByPasswordResetToken_Token(TestData.passwordResetToken).orElse(null);

        assertNull(result);
    }

    interface TestData {
        String firstName = "test";
        String email = "test@example.com";
        String password = "password";
        String passwordResetToken = "testToken";
    }
}
