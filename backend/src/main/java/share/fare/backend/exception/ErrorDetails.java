package share.fare.backend.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timeStamp, String message, String errorDescription, Integer numberOfErrors) {
}
