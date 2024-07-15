package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    @Size(min = 6, max = 30)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
