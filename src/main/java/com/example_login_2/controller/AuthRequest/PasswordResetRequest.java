package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordResetRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;
}
