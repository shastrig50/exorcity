package com.exorcity.common;

import java.time.Instant;
import java.util.Map;

/** Uniform error body returned by {@link GlobalExceptionHandler}. */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> fieldErrors) {
}
