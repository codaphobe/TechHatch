package api.techhatch.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobSearchFilter {

    @NotBlank(message = "Keyword is required")
    private String keyword;
    private String location;
    private String experienceLevel;
    private String JobType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private int page=0;
}
