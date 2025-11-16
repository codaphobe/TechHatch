package api.techhatch.com.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String companyName;
    private String companyDescription;
    private String companyLogoUrl;
    private String phone;
    private String companyWebsite;
    private String companySize;
    private String industry;
    private String location;
    private Boolean isProfileComplete;
    private String message;
}