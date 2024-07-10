package com.example_login_2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class EmailConfirm extends BaseModel implements Serializable {

    @OneToOne(mappedBy = "emailConfirm")
    @JsonBackReference
    private User user;

    @Column
    private String token;

    @Column
    private Date expiresAt;

    @Column
    private boolean activated;
}
