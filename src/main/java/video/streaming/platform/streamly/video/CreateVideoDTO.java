package video.streaming.platform.streamly.video;

import jakarta.validation.constraints.NotNull;

public record CreateVideoDTO(
        @NotNull
        String title,
        @NotNull
        String description,
        @NotNull
        String mimeType
) {
}
