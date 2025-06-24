package share.fare.backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsNotInGroupException extends RuntimeException {
    public UserIsNotInGroupException(String message) {
        super(message);
    }
}
