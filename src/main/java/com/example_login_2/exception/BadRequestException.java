package com.example_login_2.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException validateException(String message) {
        return new BadRequestException(message);
    }

    public static BadRequestException activateNoToken() {
        return new BadRequestException("Activation failed: user does not have a token.");
    }

    public static BadRequestException resendActivationNoToken() {
        return new BadRequestException("Resending verification email failed: User does not have token.");
    }

    public static BadRequestException requestNoToken() {
        return new BadRequestException("Failed : Token is missing");
    }

}
