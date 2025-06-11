package share.fare.backend.exception;

public class UserIsNotGroupOwner extends RuntimeException {
    public UserIsNotGroupOwner(Long userId, Long groupId) {
        super("User " + userId + " is not group owner of " + groupId);
    }
}
