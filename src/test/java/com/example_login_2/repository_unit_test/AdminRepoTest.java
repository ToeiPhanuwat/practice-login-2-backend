package com.example_login_2.repository_unit_test;

import com.example_login_2.model.User;
import com.example_login_2.repository.AdminRepository;
import com.example_login_2.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AdminRepoTest {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AuthRepository authRepository;

    @Test
    public void testFindByRoles_Found() {
        String USER = "ROLE_USER";
        String ADMIN = "ROLE_ADMIN";

        User user = new User()
                .setFirstName("test")
                .setEmail("test@example.com")
                .setPassword("password")
                .setRoles(new HashSet<>(Collections.singleton(USER)));
        authRepository.save(user);

        User user2 = new User()
                .setFirstName("test")
                .setEmail("test2@example.com")
                .setPassword("password")
                .setRoles(new HashSet<>(Collections.singleton(USER)));
        authRepository.save(user2);

        User user3 = new User()
                .setFirstName("test")
                .setEmail("test3@example.com")
                .setPassword("password")
                .setRoles(new HashSet<>(Collections.singleton(ADMIN)));
        authRepository.save(user3);

        List<User> resultUser = adminRepository.findByRoles(USER);
        List<User> resultAdmin = adminRepository.findByRoles(ADMIN);

        assertEquals(2, resultUser.size());
        assertEquals(1, resultAdmin.size());
    }

    @Test
    public void testFindByRoles_NotFound() {
        String ROLE = "ROLE_TEST";

        List<User> result = adminRepository.findByRoles(ROLE);

        assertEquals(0, result.size());
    }
}
