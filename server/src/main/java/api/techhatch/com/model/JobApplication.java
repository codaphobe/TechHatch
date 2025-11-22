package api.techhatch.com.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "job_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplication {

    public enum Status{
        APPLIED,UNDER_REVIEW,SHORTLISTED,INTERVIEW,REJECTED,OFFERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "job_id", updatable = false)
    private Job job;
    @ManyToOne
    @JoinColumn(name = "candidate_id", updatable = false)
    private CandidateProfile candidateProfile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "cover_letter")
    private String coverLetter;
    @Column(name = "recruiter_notes")
    private String recruiterNotes;
    @Column(name = "applied_date", updatable = false)
    private LocalDateTime appliedDate;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate(){
        appliedDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PostPersist
    protected void onUpdate(){
        lastUpdated = LocalDateTime.now();
    }
}
