package share.fare.backend.exception;

public class FriendshipNotFoundException extends RuntimeException {
    public FriendshipNotFoundException() {
        super("Friendship not found");
    }
}