package video.streaming.platform.streamly.video;

public record ResponseVideoDTO(
        String title,
        String description,
        VideoStatus status,
        Long durationSeconds,
        String storagePath
) {
}
