package api.techhatch.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String recruiterNotes;
}
