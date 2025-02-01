package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ActionIsNotAllowedException extends RuntimeException {
    public ActionIsNotAllowedException(String message) {
        super(message);
    }
}
