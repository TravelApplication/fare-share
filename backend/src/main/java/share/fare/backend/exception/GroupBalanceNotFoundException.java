package share.fare.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class GroupBalanceNotFoundException extends RuntimeException {
    public GroupBalanceNotFoundException(Long groupId, Long userId) {
        super("User with ID: " + userId + " not found in group with ID: " + groupId);
    }
}
