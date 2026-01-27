package video.streaming.platform.streamly.video.processing;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VideoProcessingPublisher {

    @Value("${spring.rabbitmq.queue-video-name}")
    private String queuename;

    private final RabbitTemplate rabbitTemplate;
    public VideoProcessingPublisher(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }

    public void sendMessage(UUID videoId, String path){

        ProcessingMessageDTO processingMessageDTO = new ProcessingMessageDTO(videoId, path);

        rabbitTemplate.convertAndSend(queuename, processingMessageDTO);
    }


}
