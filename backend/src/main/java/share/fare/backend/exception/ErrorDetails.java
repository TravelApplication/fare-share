package share.fare.backend.exception;

import java.time.LocalDateTime;

/**
 * A record representing validation error details.
 *
 * @param timeStamp the time the validation error occurred
 * @param message the validation error message
 * @param errorDescription a map of field names to error messages
 * @param numberOfErrors the number of validation errors
 */
public record ErrorDetails(LocalDateTime timeStamp, String message, String errorDescription, Integer numberOfErrors) {
}
