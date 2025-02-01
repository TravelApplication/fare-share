package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class InvitationAlreadyExistsException extends RuntimeException {
    public InvitationAlreadyExistsException(String message) {
        super(message);
    }
}