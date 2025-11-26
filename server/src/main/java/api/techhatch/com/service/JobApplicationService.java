package api.techhatch.com.service;

import api.techhatch.com.dto.request.ApplicationStatusUpdateRequest;
import api.techhatch.com.dto.request.JobApplicationRequest;
import api.techhatch.com.dto.response.JobApplicationResponse;
import api.techhatch.com.exception.BadRequestException;
import api.techhatch.com.exception.DuplicateResourceException;
import api.techhatch.com.exception.ResourceNotFoundException;
import api.techhatch.com.exception.UnauthorizedException;
import api.techhatch.com.model.CandidateProfile;
import api.techhatch.com.model.Job;
import api.techhatch.com.model.JobApplication;
import api.techhatch.com.model.RecruiterProfile;
import api.techhatch.com.repository.CandidateProfileRepo;
import api.techhatch.com.repository.JobApplicationRepo;
import api.techhatch.com.repository.JobRepo;
import api.techhatch.com.repository.RecruiterProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

    private final JobApplicationRepo applicationRepo;
    private final JobRepo jobRepo;
    private final CandidateProfileRepo candidateProfileRepo;
    private final RecruiterProfileRepo recruiterProfileRepo;
    private static final int PAGE_SIZE = 15;

    /**
     * Apply to a job (candidate only)
     * @param email candidate email
     * @param request contains application parameters
     * @return Application response
     */
    public JobApplicationResponse applyJob(String email, JobApplicationRequest request) {

        //fetch the job candidate is applying to
        Job job = jobRepo.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        //retrieve the candidate applying
        CandidateProfile candidateProfile = candidateProfileRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        //check if the profile is completed
        if(!candidateProfile.isProfileComplete()){
            throw new BadRequestException("Please complete your candidate profile before applying");
        }

        //check if the job is active
        if(job.getJobStatus() != Job.JobStatus.ACTIVE){
            throw new BadRequestException("This job posting is no longer active");
        }

        //check if job is expired (default:60 days)
        if(job.isExpired()){
            throw new BadRequestException("This job posting is expired");
        }

        //duplicate application for the same job
        if (applicationRepo.existsByJobIdAndCandidateProfileId(job.getId(), candidateProfile.getId())){
            throw new DuplicateResourceException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidateProfile(candidateProfile)
                .status(JobApplication.Status.APPLIED)
                .coverLetter(request.getCoverLetter())
                .build();

        //save application
        JobApplication savedApplication = applicationRepo.save(application);

        //increase job applicants count
        job.setApplicationCount(job.getApplicationCount() + 1);
        jobRepo.save(job);

        JobApplicationResponse response = mapToResponse(savedApplication, true, false);
        response.setMessage("Application submitted successfully");
        return response;
    }

    /**
     * Get all candidate's applications (candidate)
     * @param email candidate email
     * @param statusFilter filter applications by status
     * @param page page number
     * @return All job applications with pagination
     */
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getMyJobApplications(String email, String statusFilter, int page) {

        CandidateProfile candidateProfile = candidateProfileRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "appliedDate"));

        JobApplication.Status status = parseStatus(statusFilter);

        Page<JobApplication> applications = applicationRepo.findCandidateApplications(candidateProfile.getId(), status, pageable);

        return applications.map(a -> mapToResponse(a, true, false));
    }

    /**
     * Get applications for a job (recruiter)
     * @param email candidate email
     * @param jobId jobId to view all applications
     * @param statusFilter applications by status
     * @param page page number
     * @return all applications for the job
     */
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getJobApplications(String email, Long jobId, String statusFilter, int page) {

        RecruiterProfile recruiterProfile = recruiterProfileRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter Profile not found"));

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        //check if recruiter is accessing only his own job applicants
        if(!job.getRecruiterProfile().getId().equals(recruiterProfile.getId())){
            throw new UnauthorizedException("You can only view your job applications");
        }

        JobApplication.Status status = parseStatus(statusFilter);

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "appliedDate"));

        Page<JobApplication> applications = applicationRepo.findJobApplications(jobId, status, pageable);

        return applications.map(a -> mapToResponse(a,false, true));
    }

    /**
     * Get view detailed application (both recruiters and candidates)
     * @param email user email
     * @param applicationId to view the detailed application
     * @param userRole to find user who is requesting
     * @return detailed application
     */
    @Transactional(readOnly = true)
    public JobApplicationResponse getApplicationById(String email, Long applicationId, String userRole) {

        JobApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify authorization based on role
        if (userRole.equals("CANDIDATE")) {
            CandidateProfile candidate = candidateProfileRepo.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

            if (!application.getCandidateProfile().getId().equals(candidate.getId())) {
                throw new UnauthorizedException("You can only view your own applications");
            }

            return mapToResponse(application, true, false);

        } else if (userRole.equals("RECRUITER")) {
            RecruiterProfile recruiter = recruiterProfileRepo.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

            if (!application.getJob().getRecruiterProfile().getId().equals(recruiter.getId())) {
                throw new UnauthorizedException("You can only view applications for your own jobs");
            }

            return mapToResponse(application, false, true);

        } else {
            throw new UnauthorizedException("Invalid user role");
        }
    }

    /**
     * Update the application details
     * @param email user email
     * @param applicationId to update the application
     * @param request contains the parameters that need to be updated
     * @return updated application details
     */
    public JobApplicationResponse updateApplicationStatus(
            String email,
            Long applicationId,
            ApplicationStatusUpdateRequest request) {

        RecruiterProfile recruiter = recruiterProfileRepo.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        JobApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getRecruiterProfile().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You can only update applications for your own jobs");
        }

        // Parse and validate status
        JobApplication.Status newStatus = parseStatus(request.getStatus());
        if (newStatus == null) throw new ResourceNotFoundException("Invalid application status");

        // Update status
        application.setStatus(newStatus);
        application.setRecruiterNotes(request.getRecruiterNotes());

        JobApplication updated = applicationRepo.save(application);

        // TODO: Send notification to candidate (Day 7)
        // TODO: Send email to candidate about status change (Day 7)

        JobApplicationResponse response = mapToResponse(updated, true, true);
        response.setMessage("Application status updated successfully");
        return response;
    }

    //Helper methods
    private JobApplicationResponse mapToResponse(
            JobApplication application,
            boolean includeJobInfo,
            boolean includeCandidateInfo) {

        JobApplicationResponse.JobApplicationResponseBuilder builder = JobApplicationResponse.builder()
                .id(application.getId())
                .status(application.getStatus().toString())
                .coverLetter(application.getCoverLetter())
                .recruiterNotes(application.getRecruiterNotes())
                .appliedDate(application.getAppliedDate().toString())
                .lastUpdated(application.getLastUpdated().toString());

        // Include job info (for candidate view)
        if (includeJobInfo) {
            Job job = application.getJob();
            builder.job(JobApplicationResponse.JobInfo.builder()
                    .jobId(job.getId())
                    .title(job.getTitle())
                    .companyName(job.getRecruiterProfile().getCompanyName())
                    .location(job.getLocation())
                    .jobType(job.getJobType().toString())
                    .build());
        }

        // Include candidate info (for recruiter view)
        if (includeCandidateInfo) {
            CandidateProfile candidate = application.getCandidateProfile();
            builder.candidate(JobApplicationResponse.CandidateInfo.builder()
                    .candidateId(candidate.getId())
                    .fullName(candidate.getFullName())
                    .email(candidate.getUser().getEmail())
                    .phone(candidate.getPhone())
                    .resumeUrl(candidate.getResumeUrl())
                    .experienceYears(candidate.getExperienceYears())
                    .location(candidate.getLocation())
                    .build());
        }

        return builder.build();
    }

    private JobApplication.Status parseStatus(String statusFilter){
        if(statusFilter == null || statusFilter.isEmpty()){
            return null;
        }
        try{
            return JobApplication.Status.valueOf(statusFilter.toUpperCase());
        }catch (IllegalArgumentException e){
            return null;
        }
    }

}
