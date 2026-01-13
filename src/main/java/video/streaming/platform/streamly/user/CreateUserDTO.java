package video.streaming.platform.streamly.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateUserDTO(
        @NotNull
        String name,
        @NotNull
        @Email
        String email,
        @NotNull
        @Min(5)
        String password
) {
}
