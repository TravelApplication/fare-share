package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserAlreadyInGroupException extends RuntimeException {
    public UserAlreadyInGroupException(String message) {
        super(message);
    }
}
