package api.techhatch.com.controller;

import api.techhatch.com.dto.request.CandidateProfileRequest;
import api.techhatch.com.dto.response.CandidateProfileResponse;
import api.techhatch.com.exception.ResourceNotFoundException;
import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/candidate")
public class CandidateProfileController {


    private final CandidateProfileService candidateService;

    /**
     * Create or update candidate profile
     * @PreAuthorize ensures only CANDIDATE role can access
     * @AuthenticationPrincipal used to extract email from UserPrinciple, this ensures only the actual user updates his profile
     * @param userPrinciple fetched from AuthenticationPrincipal
     * @param request contains the complete profile details
     * @return complete updated profile
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> createOrUpdateCandidateProfile(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestBody CandidateProfileRequest request){

        CandidateProfileResponse response = candidateService.createOrUpdateProfile(userPrinciple.getUsername(), request);

        return ResponseEntity.ok(response);
    }

    /**
     * Get my candidate profile
     * @param userPrinciple has current authorised user email, fetched from @AuthenticationPrincipal
     * @return candidate profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> getMyCandidateProfile(
            @AuthenticationPrincipal UserPrinciple userPrinciple){
        CandidateProfileResponse candidateProfileResponse = candidateService.getMyProfile(userPrinciple.getUsername());

        return ResponseEntity.ok(candidateProfileResponse);
    }

    /**
     * Get candidate profile by id - for recruiters to view candidate profile
     * @param profileId candidate profile id
     * @return candidate profile
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<CandidateProfileResponse> getCandidateProfileById(@PathVariable Long profileId){

        CandidateProfileResponse candidateProfileResponse = candidateService.getProfileById(profileId);
        return ResponseEntity.ok(candidateProfileResponse);
    }

}
