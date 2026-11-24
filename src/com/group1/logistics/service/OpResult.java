package com.group1.logistics.service;

/**
 * Standardized result wrapper for all business operations.
 * Allows UI and test classes to receive clean success/failure status and user-facing messages.
 */
public class OpResult {
    private final boolean success;
    private final String message;

    public OpResult(boolean success, String message) {
        this.success = success;
        this.message = message != null ? message : "";
    }

    public static OpResult ok(String message) {
        return new OpResult(true, message);
    }

    public static OpResult err(String message) {
        return new OpResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return (success ? "[SUCCESS] " : "[FAILED] ") + message;
    }
}
