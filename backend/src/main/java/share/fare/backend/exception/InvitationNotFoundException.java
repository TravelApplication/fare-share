package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class InvitationNotFoundException  extends RuntimeException {
    public InvitationNotFoundException(String message) {
        super(message);
    }
}
