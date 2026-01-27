package video.streaming.platform.streamly.video.processing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import video.streaming.platform.streamly.video.VideoService;
import video.streaming.platform.streamly.video.VideoStatus;

@Component
public class VideoProcessingSubscriber {

    private VideoService videoService;

    public VideoProcessingSubscriber(VideoService videoService){
        this.videoService=videoService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue-video-name}")
    public void processVideo(ProcessingMessageDTO processingMessageDTO) {
        System.out.println("Mensagem recebida: ");
        System.out.println(processingMessageDTO.getVideoId());
        System.out.println(processingMessageDTO.getPath());


        // Pegar os segundos do FFmpeg
        videoService.updateVideoOnProcessingFinished(
                processingMessageDTO.getVideoId(),
                VideoStatus.UPLOADED,
                Long.parseLong("1000"),
                processingMessageDTO.getPath()
        );
    }
}
