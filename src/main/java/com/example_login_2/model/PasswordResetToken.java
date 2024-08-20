package com.example_login_2.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Embeddable
public class PasswordResetToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    private Instant expiresAt;
}
