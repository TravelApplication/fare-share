package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class InvalidExpenseException extends RuntimeException {
    public InvalidExpenseException(String message) {
        super(message);
    }
}
