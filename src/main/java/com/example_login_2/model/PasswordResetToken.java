package com.example_login_2.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Embeddable
public class PasswordResetToken {

    private String token;

    private Instant expiresAt;
}
