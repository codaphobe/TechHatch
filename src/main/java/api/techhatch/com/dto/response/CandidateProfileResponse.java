package api.techhatch.com.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private List<String> skills;
    private Integer experienceYears;
    private String education;
    private String bio;
    private String resumeUrl;
    private String profilePictureUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String location;
    private Boolean isProfileComplete;
}
