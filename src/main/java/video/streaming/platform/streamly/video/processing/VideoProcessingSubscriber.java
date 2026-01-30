package video.streaming.platform.streamly.video.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import video.streaming.platform.streamly.video.VideoDownloadService;
import video.streaming.platform.streamly.video.VideoService;
import video.streaming.platform.streamly.video.VideoStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class VideoProcessingSubscriber {

    private VideoService videoService;
    private FfmpegService ffmpegService;
    private VideoDownloadService videoDownloadService;
    private static final Logger log = LoggerFactory.getLogger(VideoProcessingSubscriber.class);

    public VideoProcessingSubscriber(VideoService videoService, FfmpegService ffmpegService, VideoDownloadService videoDownloadService){
        this.videoService=videoService;
        this.ffmpegService=ffmpegService;
        this.videoDownloadService=videoDownloadService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue-video-name}")
    public void processVideo(ProcessingMessageDTO processingMessageDTO) throws IOException {

        log.info("Buscando video no Supabase");
        Path localFile = videoDownloadService.downloadToTempVideo(processingMessageDTO.getPath());

        log.info("Enviando ao FFm para calcular duracao do video");
        long duration = ffmpegService.getVideoDurationSeconds(localFile);


        log.info("Atualizando dados do video");
        videoService.updateVideoOnProcessingFinished(
                processingMessageDTO.getVideoId(),
                VideoStatus.UPLOADED,
                duration,
                processingMessageDTO.getPath()
        );

        Files.delete(localFile);
    }

}
