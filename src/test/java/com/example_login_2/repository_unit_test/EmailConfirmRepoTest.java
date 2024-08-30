package com.example_login_2.repository_unit_test;

import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;
import com.example_login_2.repository.AuthRepository;
import com.example_login_2.repository.EmailConfirmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailConfirmRepoTest {

    @Autowired
    private EmailConfirmRepository repository;

    @Autowired
    private AuthRepository authRepository;

    @Test
    public void testFindByToken_Found() {
        EmailConfirm emailConfirm = new EmailConfirm()
                .setToken(TestData.token);
        EmailConfirm saved = repository.save(emailConfirm);

        Optional<EmailConfirm> result = repository.findByToken(saved.getToken());

        assertTrue(result.isPresent());
        assertEquals(TestData.token, result.get().getToken());
    }

    @Test
    public void testFindByToken_NotFound() {
        String token = "nonExistentToken";

        Optional<EmailConfirm> result = repository.findByToken(token);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUserId_Found() {
        User user = new User()
                .setEmail("test@example.com")
                .setPassword("password");
        User savedUser = authRepository.save(user);

        EmailConfirm savedEmail = new EmailConfirm()
                .setUser(savedUser)
                .setToken("testToken");
        repository.save(savedEmail);
        savedUser.setEmailConfirm(savedEmail);

        EmailConfirm result = repository.findByUserId(savedUser.getId()).orElse(null);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getUser().getId());
    }

    @Test
    public void testFindByUserId_NotFound() {
        long testId = 999L;

        EmailConfirm result = repository.findByUserId(testId).orElse(null);

        assertNull(result);
    }

    @Test
    public void testFindByActivatedFalse_Found() {
        User user = new User()
                .setEmail("test@example.com")
                .setPassword("password");
        User savedUser = authRepository.save(user);

        EmailConfirm savedEmail = new EmailConfirm()
                .setUser(savedUser)
                .setActivated(false);
        repository.save(savedEmail);
        savedUser.setEmailConfirm(savedEmail);

        List<User> users = repository.findByActivatedFalse();

        assertEquals(1, users.size());
//        assert users.size() > 0: "Users not found";
    }

    @Test
    public void testFindByActivatedFalse_NotFound() {
        User user = new User()
                .setEmail("test@example.com")
                .setPassword("password");
        User savedUser = authRepository.save(user);

        EmailConfirm savedEmail = new EmailConfirm()
                .setUser(savedUser)
                .setActivated(true);
        repository.save(savedEmail);
        savedUser.setEmailConfirm(savedEmail);

        List<User> users = repository.findByActivatedFalse();

        assertEquals(0, users.size());
    }


    interface TestData {
        String token = "testtesttesttesttesttesttestte";
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofHours(1)));
    }

}
