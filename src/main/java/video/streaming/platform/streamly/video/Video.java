package video.streaming.platform.streamly.video;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import video.streaming.platform.streamly.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "videos", schema = "streamly")
@EntityListeners(AuditingEntityListener.class)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private VideoStatus status;
    private Long durationSeconds;
    private String storagePath;
    private String thumbnailPath;
    private String mimeType;
    private Long sizeBytes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ownerId",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_videoOwner")
    )
    private User owner;


    public Video() {}
    public Video(String title, String description, VideoStatus status, String mimeType, Long sizeBytes, User owner){
        this.title=title;
        this.description=description;
        this.status=status;
        this.mimeType=mimeType;
        this.sizeBytes=sizeBytes;
        this.owner=owner;
    }

    public UUID getId(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public Long getDurationSeconds(){return durationSeconds;}
    public String getStoragePath(){return storagePath;}
    public String getThumbnailPath(){return thumbnailPath;}
    public String getMimeType(){return mimeType;}
    public Long getSizeBytes(){return sizeBytes;}
    public User getOwner(){return owner;}

    public void setTitle(String title) {this.title=title;}
    public void setDescription(String description) {this.description=description;}
    public void setStatus(VideoStatus status) {this.status=status;}
    public void setDurationSeconds(Long durationSeconds) {this.durationSeconds=durationSeconds;}
    public void setStoragePath(String storagePath) {this.storagePath=storagePath;}
    public void setThumbnailPath(String thumbnailPath) {this.thumbnailPath=thumbnailPath;}
    public void setMimeType(String mimeType) {this.mimeType=mimeType;}
    public void setSizeBytes(Long sizeBytes) {this.sizeBytes=sizeBytes;}
    public void setOwner(User owner) {this.owner=owner;}
}
