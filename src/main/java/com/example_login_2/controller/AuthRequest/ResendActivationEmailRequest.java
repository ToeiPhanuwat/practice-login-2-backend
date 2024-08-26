package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

;

@Getter
@Setter
public class ResendActivationEmailRequest {

    @NotEmpty
    private String token;
}
