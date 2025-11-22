package api.techhatch.com.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {

    private Long id;
    private String status;
    private String coverLetter;
    private String recruiterNotes;
    private String appliedDate;
    private String lastUpdated;

    private JobInfo job;

    private CandidateInfo candidate;

    private String message;

    //for candidate
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class JobInfo{
        private Long jobId;
        private String companyName;
        private String title;
        private String location;
        private String jobType;
    }

    //For recruiters
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class CandidateInfo{
        private Long candidateId;
        private String fullName;
        private String email;
        private String phone;
        private String resumeUrl;
        private int experienceYears;
        private String location;
    }
}
