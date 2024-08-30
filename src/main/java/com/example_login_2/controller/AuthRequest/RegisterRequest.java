package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotEmpty
    @Email
    @Size(min = 6, max = 30)
    private String email;

    @NotEmpty
    @Size(min = 8, max = 30)
    private String password;
}
