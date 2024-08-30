package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {

    @NotEmpty
    private String token;

    @NotEmpty
    @Size(min = 8, max = 20)
    private String newPassword;
}
