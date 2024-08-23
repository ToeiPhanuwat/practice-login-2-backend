package com.example_login_2.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class RoleUpdateRequest {

    @NotEmpty
    private String role;
}
