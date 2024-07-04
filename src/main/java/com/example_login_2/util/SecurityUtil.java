package com.example_login_2.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SecurityUtil {

    public static String generateToken() {
        List<CharacterRule> rules = Arrays.asList(

                new CharacterRule(EnglishCharacterData.UpperCase, 10),

                new CharacterRule(EnglishCharacterData.LowerCase, 10),

                new CharacterRule(EnglishCharacterData.Digit, 10)
        );

        PasswordGenerator generator = new PasswordGenerator();

        return generator.generatePassword(30, rules);
    }

    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();

        Object principal = authentication.getPrincipal();
        if (principal == null) return Optional.empty();

        Long userId = (Long) principal;
        return Optional.of(userId);
    }
}
