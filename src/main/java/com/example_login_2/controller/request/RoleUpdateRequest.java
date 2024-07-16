package com.example_login_2.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RoleUpdateRequest {

    @NotBlank
    private String role;
}
