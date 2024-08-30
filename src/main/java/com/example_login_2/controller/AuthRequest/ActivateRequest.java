package com.example_login_2.controller.AuthRequest;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateRequest {

    @NotEmpty
    private String token;
}
