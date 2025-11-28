package api.techhatch.com.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    private String phone;
    @Column(name = "experience_years")
    private Integer experienceYears;
    private String education;
    @Column(columnDefinition = "JSON")
    private String skills;
    @Column(columnDefinition = "TEXT")
    private String bio;
    @Column(name = "resume_url")
    private String resumeUrl;
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    @Column(name = "portfolio_url")
    private String portfolioUrl;
    @Column(name = "github_url")
    private String githubUrl;
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    private String location;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isProfileComplete(){
        return StringUtils.hasText(fullName) &&
                StringUtils.hasText(skills);
                // && StringUtils.hasText(resumeUrl);
    }
}
