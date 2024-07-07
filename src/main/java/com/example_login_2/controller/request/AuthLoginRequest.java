package com.example_login_2.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AuthLoginRequest {

    @NotEmpty
    @Size(min = 6, max = 30)
    @Email
    private String email;

    @NotEmpty
    @Size(min = 8, max = 20)
    private String password;
}
