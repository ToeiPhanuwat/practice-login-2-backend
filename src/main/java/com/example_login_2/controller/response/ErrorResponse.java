package com.example_login_2.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ErrorResponse {

    private String timestamp;

    private int status;

    private String error;

    private String message;
}
