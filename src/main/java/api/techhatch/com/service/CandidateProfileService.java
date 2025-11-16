package api.techhatch.com.service;

import api.techhatch.com.dto.request.CandidateProfileRequest;
import api.techhatch.com.dto.response.CandidateProfileResponse;
import api.techhatch.com.model.CandidateProfile;
import api.techhatch.com.model.Users;
import api.techhatch.com.repository.CandidateProfileRepo;
import api.techhatch.com.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateProfileService {

    private final CandidateProfileRepo candidateRepo;
    private final UserRepo userRepo;
    @Autowired
    private final ObjectMapper objectMapper;


    /**
     * Service to create/update candidate profile
     * @param email candidate email
     * @param request has complete profile details to create/update
     * @return complete candidate profile
     */
    public CandidateProfileResponse createOrUpdateProfile(String email, CandidateProfileRequest request){

        Users user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Get current profile or create new one
        CandidateProfile candidateProfile = candidateRepo.findByUserId(user.getId())
                .orElse(new CandidateProfile());

        //Set candidate profile user
        candidateProfile.setUser(user);

        //Update profile with requests
        updateProfileDetails(candidateProfile, request);


        CandidateProfile savedProfile = candidateRepo.save(candidateProfile);
        return mapToResponse(savedProfile);
    }

    /**
     * Get current user's profile
     */
    public CandidateProfileResponse getMyProfile(String email) {

        CandidateProfile profile = candidateRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile);
    }

    /**
     * Get any candidate's profile by ID (public - for recruiters to view)
     */
    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfileById(Long profileId) {

        CandidateProfile profile = candidateRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile);
    }

    //TODO : service to upload resume
    public void uploadResume(String email, MultipartFile file){}

    //TODO : service to upload profile picture
    public void uploadProfilePicture(String email, MultipartFile file){}

    private void updateProfileDetails(CandidateProfile profile, CandidateProfileRequest request) {
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setSkills(convertSkillsToJson(request.getSkills()));
        profile.setExperienceYears(request.getExperienceYears());
        profile.setEducation(request.getEducation());
        profile.setBio(request.getBio());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setLocation(request.getLocation());
    }

    private String convertSkillsToJson(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert skills to JSON", e);
        }
    }

    private List<String> convertJsonToSkills(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.asList(objectMapper.readValue(json, String[].class));
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private CandidateProfileResponse mapToResponse(CandidateProfile profile) {
        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .email(profile.getUser().getEmail())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .skills(convertJsonToSkills(profile.getSkills()))
                .experienceYears(profile.getExperienceYears())
                .education(profile.getEducation())
                .bio(profile.getBio())
                .resumeUrl(profile.getResumeUrl())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .location(profile.getLocation())
                .isProfileComplete(profile.isProfileComplete())
                .message("Profile updated")
                .build();
    }
}
