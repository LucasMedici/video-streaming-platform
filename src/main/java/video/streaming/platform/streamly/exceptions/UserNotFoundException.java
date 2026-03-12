package video.streaming.platform.streamly.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID uuid) {
        super("User not found with id: " + uuid);
    }
}
