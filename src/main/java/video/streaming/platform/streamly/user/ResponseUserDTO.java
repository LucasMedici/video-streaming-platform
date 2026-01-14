package video.streaming.platform.streamly.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseUserDTO(
        UUID id,
        String name,
        UserRoles role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
