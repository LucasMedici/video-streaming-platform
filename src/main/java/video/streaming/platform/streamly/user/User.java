package video.streaming.platform.streamly.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "streamly")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Email
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User() {}
    public User(String name, String email, String passwordHash, UserRoles role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getId(){return id;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public UserRoles getRole(){return role;}
    public LocalDateTime getCreatedAt(){return createdAt;}

    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public void setRole(UserRoles role){
        this.role = role;
    }

}
