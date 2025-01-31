package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class OwnerDeletionException extends RuntimeException {
    public OwnerDeletionException(String message) {
        super(message);
    }
}
