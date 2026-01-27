package video.streaming.platform.streamly.video.processing;

import java.io.Serializable;
import java.util.UUID;

public class ProcessingMessageDTO implements Serializable {

    private UUID videoId;
    private String path;

    public ProcessingMessageDTO(){
    }

    public ProcessingMessageDTO(UUID videoId, String path){
        this.videoId=videoId;
        this.path=path;
    }

    public UUID getVideoId(){
        return videoId;
    }

    public String getPath(){
        return path;
    }

}
