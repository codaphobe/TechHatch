package api.techhatch.com.controller;

import api.techhatch.com.dto.request.ApplicationStatusUpdateRequest;
import api.techhatch.com.dto.request.JobApplicationRequest;
import api.techhatch.com.dto.response.JobApplicationResponse;
import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;

    /**
     * Apply for a job candidate only
     * @param userPrinciple contains current logged in user details
     * @param request the body of the request entity
     * @return Application entity for success and bad request for errors
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<JobApplicationResponse> applyJob(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                                           @RequestBody JobApplicationRequest request){

        JobApplicationResponse response;

        try{
            response = applicationService.applyJob(userPrinciple.getUsername(), request);
            return ResponseEntity.ok(response);
        }catch(RuntimeException e){
             response = JobApplicationResponse.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get all jobs applied by candidate
     * @param userPrinciple contains current logged in user details
     * @param status filter by status
     * @param page page number
     * @return list of applied jobs
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Page<JobApplicationResponse>> getMyJobApplications(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page){

        Page<JobApplicationResponse> response = applicationService.getMyJobApplications(userPrinciple.getUsername(), status, page);
        return ResponseEntity.ok(response);
    }

    /**
     * Get applications for a job (recruiter)
     * @param userPrinciple contains current logged in user details
     * @param status filter by status
     * @param jobId jobId to show it applicants
     * @param page page number
     * @return all applications for a job
     */
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Page<JobApplicationResponse>> getJobApplications(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                                @PathVariable Long jobId,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(defaultValue = "0") int page){

        Page<JobApplicationResponse> response = applicationService.getJobApplications(userPrinciple.getUsername(), jobId, status, page);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long id) {

        // Extract role from UserDetails
        String role = userPrinciple.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .orElseThrow(() -> new RuntimeException("User role not found"));

        JobApplicationResponse response = applicationService.getApplicationById(
                userPrinciple.getUsername(),
                id,
                role
        );
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {

        JobApplicationResponse response = applicationService.updateApplicationStatus(userPrinciple.getUsername(), id, request);
        return ResponseEntity.ok(response);
    }

}
