package com.example_login_2.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException loginFailPasswordIncorrect() {
        return new UnauthorizedException("Login failed. Because the email or password is incorrect.");
    }

    public static UnauthorizedException unauthorized() {
        return new UnauthorizedException("User is unauthorized");
    }

//    public static UnauthorizedException unauthorized() {
//        return new UnauthorizedException("Invalid or expired token");
//    }

    public static UnauthorizedException handleExpiredToken() {
        return new UnauthorizedException("Token has been expired");
    }

    public static UnauthorizedException handleRevokedToken() {
        return new UnauthorizedException("Token has been revoked");
    }

}
