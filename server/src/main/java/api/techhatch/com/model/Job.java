package api.techhatch.com.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Job {

    public enum JobType{
        FULL_TIME,PART_TIME,CONTRACT,INTERNSHIP
    }

    public enum WorkMode{
        ONSITE,REMOTE,HYBRID
    }

    public enum ExperienceLevel{
        ENTRY,JUNIOR,MID,SENIOR,LEAD
    }

    public enum JobStatus{
        ACTIVE,CLOSED,EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false, updatable = false)
    private RecruiterProfile recruiterProfile;
    private String title;
    private String description;
    private String requirements;
    private String responsibilities;
    private String location;
    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    @Column(name = "work_mode")
    @Enumerated(EnumType.STRING)
    private WorkMode workMode;
    @Column(name = "experience_level")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    @Column(name = "salary_min", precision = 10, scale = 2)
    private BigDecimal salaryMin;
    @Column(name = "salary_max", precision = 10, scale = 2)
    private BigDecimal salaryMax;
    private String currency;
    @Column(name = "required_skills", columnDefinition = "JSON")
    private String requiredSkills;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus=JobStatus.ACTIVE;
    @Column(name = "posted_date", updatable = false, nullable = false)
    private LocalDateTime postedDate;
    @Column(name = "expiry_date", updatable = false)
    private LocalDateTime expiryDate;
    @Column(name = "view_count")
    private Integer viewCount;
    @Column(name = "application_count")
    private Integer applicationCount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        postedDate = LocalDateTime.now();
        if (applicationCount==null) applicationCount = 0;
        if (viewCount==null) viewCount = 0;
        // Set expiry date to 60 days from now if not provided
        if (expiryDate == null) {
            expiryDate = LocalDateTime.now().plusDays(60);
        }
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

}
