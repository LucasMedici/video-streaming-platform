package video.streaming.platform.streamly.video;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import video.streaming.platform.streamly.user.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    public VideoService(VideoRepository videoRepository){
        this.videoRepository=videoRepository;
    }

    public Video createVideo(MultipartFile video, CreateVideoDTO createVideoDTO){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if(!(principal instanceof User loggedUser)){
            throw new RuntimeException("Invalid authentication principal");
        }

        String mimeType = video.getContentType();
        long videoSize = video.getSize();


        var newVideo = new Video(createVideoDTO.title(),
                                createVideoDTO.description(),
                                VideoStatus.PROCESSING,
                                mimeType,
                                videoSize,
                                loggedUser);
        return videoRepository.save(newVideo);
    }

    public List<Video> getAllVideos(){
        return videoRepository.findAll();
    }

    public Video getVideoById(UUID videoId){
        return videoRepository.findById(videoId).orElseThrow();
    }

    public Video updateVideo(UUID videoId, UpdateVideoDTO updateVideoDTO){
        Video foundedVideo = videoRepository.findById(videoId).orElseThrow();

        foundedVideo.setTitle(updateVideoDTO.title());
        foundedVideo.setDescription(updateVideoDTO.description());
        foundedVideo.setStoragePath(updateVideoDTO.storagePath());
        foundedVideo.setThumbnailPath(updateVideoDTO.thumbnailPath());
        return videoRepository.save(foundedVideo);
    }

    public Video updateVideoOnProcessingFinished(UUID videoID, VideoStatus status, Long durationSeconds, String storagePath){
        Video foundedVideo = videoRepository.findById(videoID).orElseThrow();
        foundedVideo.setStatus(status);
        foundedVideo.setDurationSeconds(durationSeconds);
        foundedVideo.setStoragePath(storagePath);
        return videoRepository.save(foundedVideo);
    }

    public void deleteVideo(UUID videoId){
        videoRepository.deleteById(videoId);
    }
}
