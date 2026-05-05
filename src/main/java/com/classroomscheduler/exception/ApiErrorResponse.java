package com.classroomscheduler.exception;

import java.time.Instant;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now().toString(), status, error, message, path);
    }
}
