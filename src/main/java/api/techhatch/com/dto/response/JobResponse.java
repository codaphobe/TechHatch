package api.techhatch.com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String responsibilities;
    private String location;
    private String jobType;
    private String workMode;
    private String experienceLevel;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private String salaryRange;  // Formatted: "8-12 LPA"
    private List<String> requiredSkills;
    private String status;
    private String postedDate;
    private String expiryDate;
    private Integer viewCount;
    private Integer applicationCount;

    private CompanyInfo company;

    private String message;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyInfo {
        private Long recruiterId;
        private String companyName;
        private String companyLogoUrl;
        private String location;
    }
}
