package video.streaming.platform.streamly.video;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    ResponseVideoDTO entityToDTO(Video video);
}
