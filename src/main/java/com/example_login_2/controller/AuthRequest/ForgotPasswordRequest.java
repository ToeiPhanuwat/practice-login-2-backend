package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordRequest {

    @NotBlank
    private String email;
}
