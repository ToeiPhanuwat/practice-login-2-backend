package com.example_login_2.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException loginFailUserUnactivated() {
        return new ForbiddenException("Login failed. Because the user account has not been activated yet.");
    }
}
