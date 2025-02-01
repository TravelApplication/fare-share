package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to vote multiple times for the same activity.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException(String message) {
        super(message);
    }
}
