package api.techhatch.com.service;

import api.techhatch.com.dto.request.RecruiterProfileRequest;
import api.techhatch.com.dto.response.RecruiterProfileResponse;
import api.techhatch.com.model.RecruiterProfile;
import api.techhatch.com.model.Users;
import api.techhatch.com.repository.RecruiterProfileRepo;
import api.techhatch.com.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterProfileService {

    private final RecruiterProfileRepo recruiterRepo;
    private final UserRepo userRepo;

    /**
     * Service to create/update recruiter profile
     *
     */
    public RecruiterProfileResponse createOrUpdateProfile(String email, RecruiterProfileRequest request){

        //Fetch authenticated user
        Users user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Get existing profile or create a new one
        RecruiterProfile recruiterProfile = recruiterRepo.findByUserId(user.getId())
                .orElse(new RecruiterProfile());

        //Set candidate profile user
        recruiterProfile.setUser(user);

        //create new or update existing profile
        updateProfileFields(recruiterProfile,request);

        RecruiterProfile savedProfile = recruiterRepo.save(recruiterProfile);

        return mapToResponse(recruiterProfile,"Profile Updated");
    }

    /**
     * Get current user's profile
     */
    public RecruiterProfileResponse getMyProfile(String email) {

        RecruiterProfile profile = recruiterRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile,"");
    }

    /**
     * Get recruiter profile by ID (public - for candidates to view company)
     */
    @Transactional(readOnly = true)
    public RecruiterProfileResponse getProfileById(Long profileId) {

        RecruiterProfile profile = recruiterRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile, "");
    }

    //TODO : service to upload company logo
    public void uploadCompanyLogo(String email, MultipartFile file){}

    private void updateProfileFields(RecruiterProfile profile, RecruiterProfileRequest request) {
        profile.setCompanyName(request.getCompanyName());
        profile.setCompanyDescription(request.getCompanyDescription());
        profile.setPhone(request.getPhone());
        profile.setCompanyLogoUrl(request.getCompanyLogoUrl());
        profile.setCompanyWebsite(request.getCompanyWebsite());
        profile.setCompanySize(request.getCompanySize());
        profile.setIndustry(request.getIndustry());
        profile.setLocation(request.getLocation());
    }

    private RecruiterProfileResponse mapToResponse(RecruiterProfile profile, String message) {
        return RecruiterProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .email(profile.getUser().getEmail())
                .companyName(profile.getCompanyName())
                .companyDescription(profile.getCompanyDescription())
                .companyLogoUrl(profile.getCompanyLogoUrl())
                .phone(profile.getPhone())
                .companyWebsite(profile.getCompanyWebsite())
                .companySize(profile.getCompanySize())
                .industry(profile.getIndustry())
                .location(profile.getLocation())
                .isProfileComplete(profile.isProfileComplete())
                .message(message)
                .build();
    }

}
