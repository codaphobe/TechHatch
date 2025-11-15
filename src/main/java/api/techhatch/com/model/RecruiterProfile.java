package api.techhatch.com.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @Column(name = "user_id", unique = true, nullable = false)
    private Users user;
    @Column(name = "company_name" ,nullable = false)
    private String companyName;
    @Column(name = "company_description", nullable = false)
    private String companyDescription;
    @Column(name = "company_logo_url")
    private String companyLogoUrl;
    private String phone;
    @Column(name = "website")
    private String companyWebsite;
    @Enumerated(EnumType.STRING)
    @Column(name = "company_size", nullable = false)
    private String companySize;
    @Column(nullable = false)
    private String industry;
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

    public boolean isProfileComplete() {
        return StringUtils.hasText(companyName) &&
                StringUtils.hasText(companyDescription);
    }
}
