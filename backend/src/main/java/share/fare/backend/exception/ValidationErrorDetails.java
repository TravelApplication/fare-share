package share.fare.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorDetails(LocalDateTime timeStamp, String message, Map<String, String> errorDescription, Integer numberOfErrors) {
}
