package com.example_login_2.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException templateNotFound() {
        return new NotFoundException("Email template not found.");
    }

    public static NotFoundException tokenNotFound() {
        return new NotFoundException("Token not found for user.");
    }

    public static NotFoundException activateFail() {
        return new NotFoundException("Activation failed Because the user's token was not found.");
    }

    public static NotFoundException activateNotFound() {
        return new NotFoundException("No verification information for the user found.");
    }

    public static NotFoundException loginFailEmailNotFound() {
        return new NotFoundException("Login failed. Because the email or password is incorrect.");
    }

    public static NotFoundException resendActivationTokenlNotFound() {
        return new NotFoundException("Unable to resend verification email: User token not found");
    }

    public static NotFoundException requestNotFound() {
        return new NotFoundException("Failed : Token for user activation not found");
    }

    public static NotFoundException notFound() {
        return new NotFoundException("User not found");
    }

    public static NotFoundException addressNotFound() {
        return new NotFoundException("The user address was not found.");
    }
}
