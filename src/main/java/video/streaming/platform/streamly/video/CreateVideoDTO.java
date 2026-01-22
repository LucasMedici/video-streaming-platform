package video.streaming.platform.streamly.video;

import jakarta.validation.constraints.NotNull;
import video.streaming.platform.streamly.user.User;

import java.util.UUID;

public record CreateVideoDTO(
        @NotNull
        String title,
        @NotNull
        String description
) {
}
