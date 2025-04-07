package share.fare.backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SettlementNotFoundException extends RuntimeException {
    public SettlementNotFoundException(Long id) {
        super("Settlement with id " + id + " not found");
    }
}
