package video.streaming.platform.streamly.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

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

    public void uploadHlsDirectory(Path hlsDir, UUID videoId){
        try{
            Files.list(hlsDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> uploadSingleChunk(file, videoId));
        } catch (IOException e){
            throw new RuntimeException("Erro o listar arquivos HLS: ", e);
        }
    }

    private void uploadSingleChunk(Path file, UUID videoId) {
        try{
            String filename = file.getFileName().toString();
            String remotePath = videoId + "/hls/" + filename;

            byte[] bytes = Files.readAllBytes(file);

            webClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, remotePath)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(bytes)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (IOException e){
            throw new RuntimeException("Erro ao subir chunk: " + file.getFileName(), e);
        }
    }

    public void uploadThumbnail(Path thumbnailFile, UUID videoId) {
        try {
            String remotePath = videoId + "/thumbnail.jpg";

            byte[] bytes = Files.readAllBytes(thumbnailFile);

            webClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, remotePath)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(bytes)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao subir thumbnail: " + thumbnailFile.getFileName(), e);
        }
    }
}
