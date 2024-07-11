package com.example_login_2.controller.AuthRequest;

import lombok.Getter;

@Getter
public class PasswordResetRequest {

    private String token;

    private String newPassword;
}
