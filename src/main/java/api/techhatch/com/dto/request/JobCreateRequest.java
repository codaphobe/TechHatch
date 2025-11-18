package api.techhatch.com.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobCreateRequest {

    @NotBlank(message = "Job Title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 50, message = "Job description must be at least 50 characters")
    private String description;

    private String requirements;

    private String responsibilities;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Job mode is required")
    private String workMode;

    @NotBlank(message = "Experience level is required")
    private String expLevel;

    @Min(value = 0, message = "Minimum salary must positive")
    private BigDecimal salaryMin;

    @Min(value = 0, message = "Minimum salary must positive")
    private BigDecimal salaryMax;

    private String currency;

    @NotEmpty(message = "At least one required skill must be specified")
    private List<String> requiredSkills;

    private LocalDateTime expiryDate;
}
