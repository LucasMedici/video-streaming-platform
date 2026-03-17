package video.streaming.platform.streamly.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import video.streaming.platform.streamly.video.processing.VideoProcessingPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoUploadService videoUploadService;

    @Mock
    private VideoService videoService;

    @Mock
    private VideoProcessingPublisher videoProcessingPublisher;

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private MultipartFile multipartFile;

    private VideoController videoController;

    @BeforeEach
    void setUp() {
        videoController = new VideoController(
                new ObjectMapper(),
                videoUploadService,
                videoService,
                videoProcessingPublisher,
                videoMapper
        );
    }

    @Test
    void uploadVideoShouldReturnCreatedWhenPipelineSucceeds() throws Exception {
        UUID videoId = UUID.randomUUID();
        Video video = new Video();
        ReflectionTestUtils.setField(video, "id", videoId);

        when(videoService.createVideo(any(MultipartFile.class), any(CreateVideoDTO.class))).thenReturn(video);
        when(videoUploadService.uploadVideo(any(MultipartFile.class), eq(videoId + "/original"))).thenReturn("ok");
        doNothing().when(videoProcessingPublisher).sendMessage(videoId, videoId + "/original");

        ResponseEntity<?> response = videoController.uploadVideo(
                multipartFile,
                "{\"title\":\"Meu Video\",\"description\":\"Descricao\"}"
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(videoProcessingPublisher).sendMessage(videoId, videoId + "/original");
    }

    @Test
    void uploadVideoShouldReturnForbiddenWhenServiceThrows() throws Exception {
        when(videoService.createVideo(any(MultipartFile.class), any(CreateVideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid video type"));

        ResponseEntity<?> response = videoController.uploadVideo(
                multipartFile,
                "{\"title\":\"Meu Video\",\"description\":\"Descricao\"}"
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteVideoShouldDeleteBucketAndDatabaseRecord() {
        UUID videoId = UUID.randomUUID();
        Video existingVideo = new Video();
        ReflectionTestUtils.setField(existingVideo, "storagePath", videoId + "/hls/index.m3u8");

        when(videoService.getVideoById(videoId)).thenReturn(existingVideo);
        doNothing().when(videoService).deleteVideoBucket(existingVideo.getStoragePath());
        doNothing().when(videoService).deleteVideo(videoId);

        ResponseEntity<Void> response = videoController.deleteVideo(videoId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(videoService).deleteVideoBucket(existingVideo.getStoragePath());
        verify(videoService).deleteVideo(videoId);
    }
}
