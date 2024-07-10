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
        return new BadRequestException("Failed : Token is missing.");
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
}
