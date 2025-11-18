package api.techhatch.com.controller;

import api.techhatch.com.dto.request.JobCreateRequest;
import api.techhatch.com.dto.request.JobSearchFilter;
import api.techhatch.com.dto.response.JobResponse;
import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    /**
     * Post a new job (Recruiter only)
     */
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> postJob(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody JobCreateRequest request) {

        JobResponse response = jobService.postJob(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Search jobs with filters (Public)
     * Example: GET /api/v1/jobs?keyword=java&location=bangalore&jobType=FULL_TIME&page=0
     */
    @GetMapping
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page) {

        JobSearchFilter filter = new JobSearchFilter();
        filter.setKeyword(keyword);
        filter.setLocation(location);
        filter.setJobType(jobType);
        filter.setExperienceLevel(experienceLevel);
        filter.setMinSalary(minSalary);
        filter.setMaxSalary(maxSalary);
        filter.setPage(page);

        Page<JobResponse> jobs = jobService.searchJobs(filter);
        return ResponseEntity.ok(jobs);
    }

    /**
     * Get job by ID (Public - increments view count)
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get my posted jobs (Recruiter only)
     */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Page<JobResponse>> getMyJobs(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @RequestParam(defaultValue = "0") int page) {

        String email = userPrinciple.getUsername();
        Page<JobResponse> jobs = jobService.getMyJobs(email, page);
        return ResponseEntity.ok(jobs);
    }

    /**
     * Update job (Owner only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> updateJob(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long id,
            @Valid @RequestBody JobCreateRequest request) {

        String email = userPrinciple.getUsername();
        JobResponse response = jobService.updateJob(email, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Close job (Owner only)
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> closeJob(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long id) {

        String email = userPrinciple.getUsername();
        JobResponse response = jobService.closeJob(email, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete job (Owner only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> deleteJob(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long id) {

        String email = userPrinciple.getUsername();
        jobService.deleteJob(email, id);
        return ResponseEntity.noContent().build();
    }
}
