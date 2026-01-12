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

}
