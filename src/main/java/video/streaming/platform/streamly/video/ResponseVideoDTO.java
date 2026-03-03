package video.streaming.platform.streamly.video;

import java.util.UUID;

public record ResponseVideoDTO(
        UUID id,
        String title,
        String description,
        VideoStatus status,
        Long durationSeconds,
        String storagePath,
        String thumbnailPath
) {
}
