package video.streaming.platform.streamly.exceptions;

import java.util.UUID;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(UUID uuid) {
        super("Video not found with id: " + uuid);
    }
}
