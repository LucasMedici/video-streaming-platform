package video.streaming.platform.streamly.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class VideoDownloadService {

    @Value("${supabase.bucket}")
    private String bucket;

    private final WebClient webClient;

    public VideoDownloadService(WebClient webClient){
        this.webClient=webClient;
    }

    public Path downloadToTempVideo(String path) {
        try {
            Path tempFile = Files.createTempFile("video-", ".mp4");

            Flux<DataBuffer> dataBufferFlux = webClient.get()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, path)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);

            DataBufferUtils.write(
                    dataBufferFlux,
                    tempFile,
                    StandardOpenOption.WRITE
            ).block();

            return tempFile;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar vídeo do Supabase", e);
        }
    }

}
