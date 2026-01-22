package video.streaming.platform.streamly.video;

public record UpdateVideoDTO(
        String title,
        String description,
        String storagePath,
        String thumbnailPath
) {
}
