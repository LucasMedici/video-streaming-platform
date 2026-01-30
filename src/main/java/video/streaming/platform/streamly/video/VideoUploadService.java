package video.streaming.platform.streamly.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
public class VideoUploadService {

    @Value("${supabase.bucket}")
    private String bucket;

    private final WebClient webClient;
    public VideoUploadService(WebClient webClient){
        this.webClient=webClient;
    }

    public String uploadVideo(MultipartFile file, String path) throws IOException {
        return webClient.post()
                .uri("/storage/v1/object/{bucket}/{path}", bucket, path)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
