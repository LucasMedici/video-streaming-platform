package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import video.streaming.platform.streamly.video.processing.VideoProcessingPublisher;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private ObjectMapper objectMapper;
    private VideoUploadService videoUploadService;
    private VideoService videoService;
    private VideoProcessingPublisher videoProcessingPublisher;
    private final VideoMapper videoMapper;
    public VideoController(ObjectMapper objectMapper, VideoUploadService videoUploadService, VideoService videoService, VideoProcessingPublisher videoProcessingPublisher, VideoMapper videoMapper){
        this.objectMapper=objectMapper;
        this.videoUploadService=videoUploadService;
        this.videoService=videoService;
        this.videoProcessingPublisher=videoProcessingPublisher;
        this.videoMapper=videoMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@RequestPart("file") MultipartFile video, @RequestPart("data") String data) throws Exception {
        CreateVideoDTO createVideoDTO = objectMapper.readValue(data, CreateVideoDTO.class);

        try{
            Video createdVideo = videoService.createVideo(video, createVideoDTO); // criar linha do video no DB
            String originalPath = createdVideo.getId() + "/original";
            videoUploadService.uploadVideo(video, originalPath); // subir objeto inteiro do video no DB
            videoProcessingPublisher.sendMessage(createdVideo.getId(), originalPath); // enviar para fila processar o FFMPEG
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Arguments");
        }
    }

    @GetMapping()
    public ResponseEntity<List<ResponseVideoDTO>> getAllVideos(){
        List<Video> allVideos = videoService.getAllVideos();

        List<ResponseVideoDTO> responseVideoDTOS = allVideos.stream()
                .map(video -> videoMapper.entityToDTO(video))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseVideoDTOS);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<ResponseVideoDTO> getVideoById(@PathVariable UUID videoId){
        Video videoById = videoService.getVideoById(videoId);
        ResponseVideoDTO responseVideoDTO = videoMapper.entityToDTO(videoById);
        return ResponseEntity.status(HttpStatus.OK).body(responseVideoDTO);
    }

    @GetMapping("/{videoId}/stream")
    public ResponseEntity<?> getVideoStream(@PathVariable UUID videoId){
        String signedUrl = videoService.generatePublicUrl(videoId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("url", signedUrl));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable UUID videoId){
        Video videoToDelete = videoService.getVideoById(videoId);

        videoService.deleteVideoBucket(videoToDelete.getStoragePath());
        videoService.deleteVideo(videoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
