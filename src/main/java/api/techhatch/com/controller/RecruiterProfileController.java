package api.techhatch.com.controller;

import api.techhatch.com.dto.request.RecruiterProfileRequest;
import api.techhatch.com.dto.response.RecruiterProfileResponse;
import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/recruiter")
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterService;

    /**
     * Create or update recruiter profile
     * @PreAuthorize ensures only RECRUITER role can access
     * @AuthenticationPrincipal extracts email from UserPrinciple, this ensures only the actual user updates his profile
     * @param userPrinciple fetched from AuthenticationPrincipal
     * @param request contains the complete profile details
     * @return complete updated profile
     */
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<RecruiterProfileResponse> createOrUpdateRecruiterProfile(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestBody RecruiterProfileRequest request){

        RecruiterProfileResponse response = recruiterService.createOrUpdateProfile(userPrinciple.getUsername(), request);

        return ResponseEntity.ok(response);
    }

    /**
     * Get my recruiter profile
     * @param userPrinciple has current authorised user email, fetched from @AuthenticationPrincipal
     * @return candidate profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<RecruiterProfileResponse> getMyRecruiterProfile(
            @AuthenticationPrincipal UserPrinciple userPrinciple){
        RecruiterProfileResponse candidateProfileResponse = recruiterService.getMyProfile(userPrinciple.getUsername());

        return ResponseEntity.ok(candidateProfileResponse);
    }

    /**
     * Get recruiter profile by id - for candidate to view company/recruiter profile
     * @param profileId candidate profile id
     * @return candidate profile
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<RecruiterProfileResponse> getRecruiterProfileById(@PathVariable Long profileId){
        try{
            RecruiterProfileResponse recruiterProfileResponse = recruiterService.getProfileById(profileId);
            return ResponseEntity.ok(recruiterProfileResponse);
        }catch (RuntimeException ex){
            RecruiterProfileResponse response = RecruiterProfileResponse.builder()
                    .message(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
