package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ActivateRequest {

    @NotBlank
    private String token;
}
