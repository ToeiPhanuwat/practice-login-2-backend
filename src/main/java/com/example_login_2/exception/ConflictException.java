package com.example_login_2.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public static ConflictException createDuplicate() {
        return new ConflictException("User creates duplicate email.");
    }

    public static ConflictException activateAlready() {
        return new ConflictException("Email already activated.");
    }

    public static ConflictException userHasOneRole() {
        return new ConflictException("Cannot delete the only role assigned to the user.");
    }

}
