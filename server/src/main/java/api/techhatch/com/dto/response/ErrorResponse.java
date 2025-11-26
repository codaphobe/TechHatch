package api.techhatch.com.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private boolean success;

    private String message;

    private int status;

    private String error;

    private String path;

    private String timeStamp;

    //Validation errors
    private List<String> details;
}
