package api.techhatch.com.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchFilter {

    private String keyword;
    private String location;
    private String experienceLevel;
    private String JobType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private int page=0;
}
