package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private ObjectMapper objectMapper;
    private VideoUploadService videoUploadService;
    public VideoController(ObjectMapper objectMapper, VideoUploadService videoUploadService){
        this.objectMapper=objectMapper;
        this.videoUploadService=videoUploadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@RequestPart("file") MultipartFile video, @RequestPart("data") String data) throws Exception {
        CreateVideoDTO createVideoDTO = objectMapper.readValue(data, CreateVideoDTO.class);
        String path = UUID.randomUUID()+"-"+video.getOriginalFilename();

        String publicUrl = videoUploadService.uploadVideo(video, path);

        //TODO: chamar video service e criar a linha do video com os dados dele lá
        //TODO: com isso funcionando implementar o FFmpeg para converter os videos em partes de multiplas resolucoes
        //TODO: inserir sistema de fila com isso, ao inserir o video fica processing, depois ele atualiza a linha pra ok ou algo assim

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
