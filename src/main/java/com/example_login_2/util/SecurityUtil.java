package com.example_login_2.util;

import com.example_login_2.config.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
public class SecurityUtil {

    private SecurityUtil() {}

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

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getUserId());
        }
        return Optional.empty();
    }

    public static Optional<String> getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();

        Object principal = authentication.getPrincipal();
        if (principal == null) return Optional.empty();

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getToken());
        }
        return Optional.empty();
    }

    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();

        Object principal = authentication.getPrincipal();
        if (principal == null) return Optional.empty();

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

}
