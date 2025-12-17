package api.techhatch.com.dto.response;

import api.techhatch.com.model.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    public enum VerificationStatus{
        VERIFIED, NOT_VERIFIED, ALREADY_VERIFIED
    }

    private boolean success;
    private String message;
    private Long userId;
    private String email;
    private String role;
    private Users.AccountStatus accountStatus;
    private VerificationStatus verificationStatus;
}
