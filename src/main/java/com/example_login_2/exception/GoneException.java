package com.example_login_2.exception;

public class GoneException extends RuntimeException {
    public GoneException(String message) {
        super(message);
    }

    public static GoneException activateTokenExpire() {
        return new GoneException("The user activation token has expired.");
    }
}
