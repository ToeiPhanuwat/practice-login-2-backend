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

    public static BadRequestException requestTokenNullOrEmpty() {
        return new BadRequestException("Token cannot be null or empty.");
    }

    public static BadRequestException fileMaxSize() {
        return new BadRequestException("File size must be less than 5 MB.");
    }

    public static BadRequestException fileContentTypeIsNull() {
        return new BadRequestException("File content type must not be null.");
    }

    public static BadRequestException unsupported() {
        return new BadRequestException("Unsupported file type. Only JPEG and PNG are allowed.");
    }

    public static BadRequestException currentDirectory() {
        return new BadRequestException("Path outside current directory.");
    }

    public static BadRequestException requestNullOrEmpty() {
        return new BadRequestException("Request cannot be null or empty.");
    }

    public static BadRequestException requestNull() {
        return new BadRequestException("Request cannot be null.");
    }

    public static BadRequestException requestNewPasswordNullOrEmpty() {
        return new BadRequestException("New password cannot be null or empty.");
    }

    public static BadRequestException tokenIsMissing() {
        return new BadRequestException("Token is missing.");
    }
}
