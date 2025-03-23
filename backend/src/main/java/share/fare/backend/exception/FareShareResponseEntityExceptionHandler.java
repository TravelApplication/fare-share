package share.fare.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all exceptions
 */
@ControllerAdvice
public class FareShareResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Global exception handler for all exceptions
     * @param ex Exception
     * @param request WebRequest
     * @return ResponseEntity<ErrorDetails>
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Global exception handler for UserNotFoundException
     * @param ex UserNotFoundException
     * @param request WebRequest
     * @return ResponseEntity<ErrorDetails>
     */
    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception handler for MethodArgumentNotValidException
     * @param ex MethodArgumentNotValidException
     * @param headers HttpHeaders
     * @param status HttpStatusCode
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                LocalDateTime.now(),
                "Validation failed",
                errors,
                ex.getBindingResult().getErrorCount()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler for InvalidCredentialsException
     * @param ex InvalidCredentialsException
     * @param request WebRequest
     * @return ResponseEntity<ErrorDetails>
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public final ResponseEntity<ErrorDetails> handleInvalidCredentialsException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Global exception handler for UserAlreadyExistsException
     * @param ex UserAlreadyExistsException
     * @param request WebRequest
     * @return ResponseEntity<ErrorDetails>
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public final ResponseEntity<ErrorDetails> userAlreadyExistsException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Global exception handler for UserAlreadyInGroupException
     * @param ex UserAlreadyInGroupException
     * @param request WebRequest
     * @return ResponseEntity<ErrorDetails>
     */
    @ExceptionHandler(UserAlreadyInGroupException.class)
    public final ResponseEntity<ErrorDetails> userAlreadyInGroupException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Global exception handler for GroupNotFoundException
     */
    @ExceptionHandler(UserIsNotInGroupException.class)
    public final ResponseEntity<ErrorDetails> userIsNotInGroupException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Global exception for trying to delete the owner of a group
     */
    @ExceptionHandler(OwnerDeletionException.class)
    public final ResponseEntity<ErrorDetails> ownerDeletionException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvitationAlreadyExistsException.class)
    public final ResponseEntity<ErrorDetails> handleInvitationAlreadyExistsException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
    /**
     * Global exception for not found activity
     */
    @ExceptionHandler(ActivityNotFoundException.class)
    public final ResponseEntity<ErrorDetails> activityNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception for duplicate vote
     */
    @ExceptionHandler(DuplicateVoteException.class)
    public final ResponseEntity<ErrorDetails> duplicateVoteException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleInvitationNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception for vote not found
     */
    @ExceptionHandler(VoteNotFoundException.class)
    public final ResponseEntity<ErrorDetails> voteNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Global exception for action not allowed
     */
    @ExceptionHandler(ActionIsNotAllowedException.class)
    public final ResponseEntity<ErrorDetails> actionNotAllowedException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FriendshipNotFoundException.class)
    public final ResponseEntity<ErrorDetails> FriendshipNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public final ResponseEntity<ErrorDetails> expenseNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GroupBalanceNotFoundException.class)
    public final ResponseEntity<ErrorDetails> groupBalanceNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidExpenseException.class)
    public final ResponseEntity<ErrorDetails> invalidExpenseException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), ex.getStackTrace().length);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
