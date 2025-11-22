package api.techhatch.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobApplicationRequest {

    @NotBlank(message = "Job id is required")
    private Long jobId;

    private String coverLetter;
}