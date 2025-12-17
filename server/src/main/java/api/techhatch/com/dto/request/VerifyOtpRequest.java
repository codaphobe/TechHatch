package api.techhatch.com.dto.request;

import api.techhatch.com.model.OtpVerification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class VerifyOtpRequest {

    @Valid
    @Email(message = "Email cannot be blank")
    private String email;
    @Size(max = 6,min = 6, message = "Please provide a valid OTP")
    @NotBlank(message = "Please provide a valid OTP")
    private String otpCode;
    @NotBlank(message = "Please provide a purpose for verifying otp")
    private OtpVerification.OtpPurpose otpPurpose;
}
