package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import video.streaming.platform.streamly.video.processing.VideoProcessingPublisher;

import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private ObjectMapper objectMapper;
    private VideoUploadService videoUploadService;
    private VideoService videoService;
    private VideoProcessingPublisher videoProcessingPublisher;
    public VideoController(ObjectMapper objectMapper, VideoUploadService videoUploadService, VideoService videoService, VideoProcessingPublisher videoProcessingPublisher){
        this.objectMapper=objectMapper;
        this.videoUploadService=videoUploadService;
        this.videoService=videoService;
        this.videoProcessingPublisher=videoProcessingPublisher;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@RequestPart("file") MultipartFile video, @RequestPart("data") String data) throws Exception {
        CreateVideoDTO createVideoDTO = objectMapper.readValue(data, CreateVideoDTO.class);
        String path = UUID.randomUUID()+"-"+video.getOriginalFilename();

        Video createdVideo = videoService.createVideo(video, createVideoDTO); // criar linha do video no DB
        videoUploadService.uploadVideo(video, path); // subir objeto inteiro do video no DB
        videoProcessingPublisher.sendMessage(createdVideo.getId(), path); // enviar para fila processar o FFMPEG


        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
