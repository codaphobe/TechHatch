package api.techhatch.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecruiterProfileRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255)
    private String companyName;

    @NotBlank(message = "Company description is required")
    private String companyDescription;

    private String phone;

    private String companyLogoUrl;

    private String companyWebsite;

    @NotBlank(message = "Please select company size")
    private String companySize;  // "1-10", "11-50", etc.

    private String industry;

    private String location;
}
