package video.streaming.platform.streamly.video.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import video.streaming.platform.streamly.video.VideoDownloadService;
import video.streaming.platform.streamly.video.VideoService;
import video.streaming.platform.streamly.video.VideoStatus;
import video.streaming.platform.streamly.video.VideoUploadService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class VideoProcessingSubscriber {

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.url}")
    private String supabaseUrl;

    private VideoService videoService;
    private FfmpegService ffmpegService;
    private VideoDownloadService videoDownloadService;
    private VideoUploadService videoUploadService;
    private static final Logger log = LoggerFactory.getLogger(VideoProcessingSubscriber.class);

    public VideoProcessingSubscriber(VideoService videoService, FfmpegService ffmpegService, VideoDownloadService videoDownloadService, VideoUploadService videoUploadService){
        this.videoService=videoService;
        this.ffmpegService=ffmpegService;
        this.videoDownloadService=videoDownloadService;
        this.videoUploadService=videoUploadService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue-video-name}")
    public void processVideo(ProcessingMessageDTO processingMessageDTO) throws IOException {

        try{
            log.info("Buscando video no Supabase");
            Path localFile = videoDownloadService.downloadToTempVideo(processingMessageDTO.getPath());

            log.info("Enviando ao FFm para calcular duracao do video");
            long duration = ffmpegService.getVideoDurationSeconds(localFile);

            log.info("Gerando HLS");
            Path hlsDir = ffmpegService.generateHls(localFile);

            log.info("Gerando thumbnail");
            Path thumbnailDir = ffmpegService.generateThumb(localFile);

            log.info("Subindo chunks ao supabase");
            videoUploadService.uploadHlsDirectory(hlsDir, processingMessageDTO.getVideoId());

            log.info("Subindo thumbnail ao supabase");
            videoUploadService.uploadThumbnail(thumbnailDir, processingMessageDTO.getVideoId());


            log.info("Atualizando dados do video");
            videoService.updateVideoOnProcessingFinished(
                    processingMessageDTO.getVideoId(),
                    VideoStatus.UPLOADED,
                    duration,
                    processingMessageDTO.getPath(),
                    makeThumbnailLink(processingMessageDTO.getVideoId())
            );

            Files.delete(localFile);
        }catch (Exception e){
            log.error("Erro ao processar video {}", processingMessageDTO.getVideoId(), e);
            videoService.updateVideoStatus(VideoStatus.FAILED, processingMessageDTO.getVideoId());
        }

    }

    private String makeThumbnailLink(UUID videoId) {
        return supabaseUrl +
                "/storage/v1/object/public/" +
                bucket +
                "/" +
                videoId +
                "/thumbnail.jpg";
    }

}
