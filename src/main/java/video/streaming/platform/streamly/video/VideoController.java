package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import video.streaming.platform.streamly.video.upload.VideoUploadService;

import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private ObjectMapper objectMapper;
    private VideoUploadService videoUploadService;
    private VideoService videoService;
    public VideoController(ObjectMapper objectMapper, VideoUploadService videoUploadService, VideoService videoService){
        this.objectMapper=objectMapper;
        this.videoUploadService=videoUploadService;
        this.videoService=videoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@RequestPart("file") MultipartFile video, @RequestPart("data") String data) throws Exception {
        CreateVideoDTO createVideoDTO = objectMapper.readValue(data, CreateVideoDTO.class);
        String path = UUID.randomUUID()+"-"+video.getOriginalFilename();

        Video createdVideo = videoService.createVideo(video, createVideoDTO);
        videoUploadService.uploadVideo(video, path);
        // enviar pra fila de processamento com ffmpeg
        // mover essa chamada de service aqui de baixo para dentro do subscriber da fila
        videoService.updateVideoOnProcessingFinished(createdVideo.getId(), VideoStatus.UPLOADED, Long.parseLong("1000"));

        //TODO: FFmpeg para converter os videos em partes de multiplas resolucoes
        //TODO: ARRUMAR SEGUNDOS DO VIDEO, TA FIXADO EM 1000
        //TODO: inserir sistema de fila com isso, ao inserir o video fica processing, depois ele atualiza a linha pra ok ou algo assim

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
