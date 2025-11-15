package api.techhatch.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CandidateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String fullName;

    private String phone;
    @NotBlank(message = "Skills are required")
    private List<String> skills;  // ["Java", "Spring Boot", "React"]

    private Integer experienceYears;
    @NotBlank(message = "Education details are required")
    private String education;

    private String bio;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String location;
}