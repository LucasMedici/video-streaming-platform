package video.streaming.platform.streamly.video;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import video.streaming.platform.streamly.exceptions.VideoNotFoundException;
import video.streaming.platform.streamly.user.User;
import video.streaming.platform.streamly.user.UserRoles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private VideoService videoService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVideoShouldPersistWithAuthenticatedUser() {
        User owner = new User("Admin", "admin@mail.com", "hash", UserRoles.ADMIN);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(owner, null, owner.getAuthorities())
        );

        CreateVideoDTO dto = new CreateVideoDTO("Video A", "Descricao A");

        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Video created = videoService.createVideo(multipartFile, dto);

        assertEquals("Video A", created.getTitle());
        assertEquals("Descricao A", created.getDescription());
        assertEquals(VideoStatus.PROCESSING, created.getStatus());
        assertEquals("video/mp4", created.getMimeType());
        assertEquals(1024L, created.getSizeBytes());
        assertEquals(owner, created.getOwner());
    }

    @Test
    void createVideoShouldThrowWhenMimeTypeIsInvalid() {
        User owner = new User("Admin", "admin@mail.com", "hash", UserRoles.ADMIN);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(owner, null, owner.getAuthorities())
        );

        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(400L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> videoService.createVideo(multipartFile, new CreateVideoDTO("Video", "Desc"))
        );

        assertEquals("Invalid video type", exception.getMessage());
    }

    @Test
    void getVideoByIdShouldThrowWhenNotFound() {
        UUID videoId = UUID.randomUUID();
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        assertThrows(VideoNotFoundException.class, () -> videoService.getVideoById(videoId));
    }

    @Test
    void updateVideoOnProcessingFinishedShouldPersistProcessingData() {
        UUID videoId = UUID.randomUUID();
        Video video = new Video("Title", "Desc", VideoStatus.PROCESSING, "video/mp4", 100L, new User());

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Video updated = videoService.updateVideoOnProcessingFinished(
                videoId,
                VideoStatus.READY,
                88L,
                "videos/path/index.m3u8",
                "videos/path/thumb.jpg"
        );

        assertEquals(VideoStatus.READY, updated.getStatus());
        assertEquals(88L, updated.getDurationSeconds());
        assertEquals("videos/path/index.m3u8", updated.getStoragePath());
        assertEquals("videos/path/thumb.jpg", updated.getThumbnailPath());
    }

    @Test
    void generatePublicUrlShouldBuildExpectedPath() {
        ReflectionTestUtils.setField(videoService, "supabaseUrl", "https://demo.supabase.co");
        ReflectionTestUtils.setField(videoService, "bucket", "streamly-bucket");

        UUID videoId = UUID.randomUUID();
        String url = videoService.generatePublicUrl(videoId);

        assertTrue(url.contains("https://demo.supabase.co/storage/v1/object/public/streamly-bucket/"));
        assertTrue(url.endsWith(videoId + "/hls/index.m3u8"));
    }
}

