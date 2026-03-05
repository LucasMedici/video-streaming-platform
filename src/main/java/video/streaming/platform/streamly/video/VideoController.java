package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Video", description = "Tag to video operations")
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

    @Operation(summary = "Upload Video", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(
            @Parameter(description = "Video file")
            @RequestPart("file") MultipartFile video,
            @Parameter(description = "Video metadata JSON")
            @RequestPart("data") String data) throws Exception {
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

    @Operation(summary = "Get All Videos", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<ResponseVideoDTO>> getAllVideos(){
        List<Video> allVideos = videoService.getAllVideos();

        List<ResponseVideoDTO> responseVideoDTOS = allVideos.stream()
                .map(video -> videoMapper.entityToDTO(video))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responseVideoDTOS);
    }

    @Operation(summary = "Get Video by ID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/{videoId}")
    public ResponseEntity<ResponseVideoDTO> getVideoById(
            @Parameter(description = "ID do Video", required = true)
            @PathVariable UUID videoId){
        Video videoById = videoService.getVideoById(videoId);
        ResponseVideoDTO responseVideoDTO = videoMapper.entityToDTO(videoById);
        return ResponseEntity.status(HttpStatus.OK).body(responseVideoDTO);
    }

    @Operation(summary = "Get Video URL for Stream", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/{videoId}/stream")
    public ResponseEntity<?> getVideoStream(
            @Parameter(description = "ID do Video", required = true)
            @PathVariable UUID videoId){
        String signedUrl = videoService.generatePublicUrl(videoId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("url", signedUrl));
    }

    @Operation(summary = "Delete Video", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(
            @Parameter(description = "ID do Video", required = true)
            @PathVariable UUID videoId){
        Video videoToDelete = videoService.getVideoById(videoId);

        videoService.deleteVideoBucket(videoToDelete.getStoragePath());
        videoService.deleteVideo(videoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
