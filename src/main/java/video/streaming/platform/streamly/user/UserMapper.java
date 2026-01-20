package video.streaming.platform.streamly.user;

import jakarta.persistence.Column;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserDTO entityToDTO(User user);
}
