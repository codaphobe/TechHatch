package api.techhatch.com.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be 6+ characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}
