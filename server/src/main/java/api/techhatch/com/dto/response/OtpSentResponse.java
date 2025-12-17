package api.techhatch.com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpSentResponse {
    private boolean success;
    private String email;
    private String message;
    private boolean otpSent;
    private String error;
}
