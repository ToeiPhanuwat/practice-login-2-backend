package com.example_login_2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Entity
public class JwtToken extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column
    private String jwtToken;

    @Column
    private Instant issuedAt;

    @Column
    private Instant expiresAt;

    @Column
    private boolean isRevoked;
}
