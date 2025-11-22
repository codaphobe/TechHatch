package api.techhatch.com.service;

import api.techhatch.com.dto.request.JobCreateRequest;
import api.techhatch.com.dto.request.JobSearchFilter;
import api.techhatch.com.dto.response.JobResponse;
import api.techhatch.com.model.Job;
import api.techhatch.com.model.RecruiterProfile;
import api.techhatch.com.repository.JobRepo;
import api.techhatch.com.repository.RecruiterProfileRepo;
import api.techhatch.com.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final RecruiterProfileRepo recruiterRepo;
    private final UserRepo userRepo;
    private final JobRepo jobRepo;
    @Autowired
    private final ObjectMapper objectMapper;

    private static final int PAGE_SZE=25;

    public JobResponse postJob(String email, JobCreateRequest request){

        RecruiterProfile recruiterProfile = recruiterRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = Job.builder()
                .recruiterProfile(recruiterProfile)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .responsibilities(request.getResponsibilities())
                .location(request.getLocation())
                .jobType(Job.JobType.valueOf(request.getJobType().toUpperCase()))
                .workMode(request.getWorkMode() != null ? Job.WorkMode.valueOf(request.getWorkMode().toUpperCase()) : null)
                .expLevel(Job.ExperienceLevel.valueOf(request.getExpLevel().toUpperCase()))
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .requiredSkills(convertSkillsToJson(request.getRequiredSkills()))
                .jobStatus(Job.JobStatus.ACTIVE)
                .expiryDate(request.getExpiryDate())
                .build();

        Job savedJob = jobRepo.save(job);
        JobResponse response = mapToResponse(savedJob);
        response.setMessage("Job created and posted successfully");
        return response;
    }

    /**
     * Search job filters - Public
     * @param filter - filters for specific jobs
     * @return job listing with filters applied
     */
    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(JobSearchFilter filter){

        Job.JobType jobType = parseJobType(filter.getJobType());
        Job.ExperienceLevel expLevel = parseExperienceLevel(filter.getExperienceLevel());

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                PAGE_SZE,
                Sort.by(Sort.Direction.DESC, "postedDate")
        );

        Page<Job> jobs = jobRepo.searchJobs(
                filter.getKeyword(),
                filter.getLocation(),
                jobType,
                expLevel,
                filter.getMinSalary(),
                filter.getMaxSalary(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );
        return jobs.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id){

         Job job = jobRepo.findById(id)
                 .orElseThrow(() -> new RuntimeException("Job not found"));

         JobResponse response = mapToResponse(job);
         response.setMessage("Job fetched");
         return response;
    }

    /**
     * Get all jobs posted by recruiter
     * @return all jobs posted by recruiter with pagination
     */
    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(String email, int page){

        RecruiterProfile recruiterProfile = recruiterRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SZE,
                Sort.by(Sort.Direction.DESC, "postedDate"));

        //find jobs posted by recruiter
        Page<Job> jobs = jobRepo.findByRecruiterProfileId(recruiterProfile.getId(), pageable);

        return jobs.map(this::mapToResponse);
    }

    public JobResponse updateJob(String email, Long jobId, JobCreateRequest request){
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        RecruiterProfile recruiter = recruiterRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        // Verify ownership
        if (!job.getRecruiterProfile().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You can only update your own job postings");
        }

        // Update fields
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setResponsibilities(request.getResponsibilities());
        job.setLocation(request.getLocation());
        job.setJobType(Job.JobType.valueOf(request.getJobType().toUpperCase()));
        job.setWorkMode(request.getWorkMode() != null ? Job.WorkMode.valueOf(request.getWorkMode().toUpperCase()) : null);
        job.setExpLevel(Job.ExperienceLevel.valueOf(request.getExpLevel().toUpperCase()));
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setRequiredSkills(convertSkillsToJson(request.getRequiredSkills()));
        if (request.getExpiryDate()!=null) job.setExpiryDate(request.getExpiryDate());

        Job updated = jobRepo.save(job);

        JobResponse response = mapToResponse(updated);
        response.setMessage("Job updated successfully");
        return response;
    }

    /**
     * Close the job, for recruiters only
     * @return a job object with status closed
     */
    public JobResponse closeJob(String email, Long jobIid){

        Job job = jobRepo.findById(jobIid)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        RecruiterProfile recruiterProfile = recruiterRepo.findByUserEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (!job.getRecruiterProfile().getId().equals(recruiterProfile.getId())) {
            throw new RuntimeException("You can only close your own job postings");
        }

        job.setJobStatus(Job.JobStatus.CLOSED);

        Job savedJob = jobRepo.save(job);
        JobResponse response = mapToResponse(savedJob);
        response.setMessage("Job closed successfully");
        return response;
    }

    /**
     * Delete the job, for recruiters only
     */
    public void deleteJob(String email, Long jobId){

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        RecruiterProfile recruiterProfile = recruiterRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!job.getRecruiterProfile().getId().equals(recruiterProfile.getId())) {
            throw new RuntimeException("You can only delete your own job postings");
        }
        //delete the job
        jobRepo.delete(job);
    }

    //HELPER METHODS

    private Job.JobType parseJobType(String jobType) {
        if (jobType == null || jobType.isEmpty()) {
            return null;
        }
        try {
            return Job.JobType.valueOf(jobType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;  // Invalid job type - ignore filter
        }
    }

    private Job.ExperienceLevel parseExperienceLevel(String experienceLevel) {
        if (experienceLevel == null || experienceLevel.isEmpty()) {
            return null;
        }
        try {
            return Job.ExperienceLevel.valueOf(experienceLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;  // Invalid job type - ignore filter
        }
    }

    private String formatSalaryRange(BigDecimal min, BigDecimal max, String currency) {
        if (min == null && max == null) {
            return "Not disclosed";
        }

        if (currency.equals("INR")) {
            // Convert to LPA (Lakhs Per Annum)
            double minLPA = min != null ? min.doubleValue() / 100000 : 0;
            double maxLPA = max != null ? max.doubleValue() / 100000 : 0;

            if (min != null && max != null) {
                return String.format("%.1f - %.1f LPA", minLPA, maxLPA);
            } else if (min != null) {
                return String.format("%.1f+ LPA", minLPA);
            } else {
                return String.format("Up to %.1f LPA", maxLPA);
            }
        } else {
            // For other currencies
            if (min != null && max != null) {
                return String.format("%s %,.0f - %,.0f", currency, min, max);
            } else if (min != null) {
                return String.format("%s %,.0f+", currency, min);
            } else {
                return String.format("Up to %s %,.0f", currency, max);
            }
        }
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .responsibilities(job.getResponsibilities())
                .location(job.getLocation())
                .jobType(job.getJobType().toString())
                .workMode(job.getWorkMode() != null ? job.getWorkMode().toString() : null)
                .experienceLevel(job.getExpLevel().toString())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .salaryRange(formatSalaryRange(job.getSalaryMin(), job.getSalaryMax(), job.getCurrency()))
                .requiredSkills(convertJsonToSkills(job.getRequiredSkills()))
                .status(job.getJobStatus().toString())
                .postedDate(job.getPostedDate().toString())
                .expiryDate(job.getExpiryDate().toString())
                .viewCount(job.getViewCount())
                .applicationCount(job.getApplicationCount())
                .company(JobResponse.CompanyInfo.builder()
                        .recruiterId(job.getRecruiterProfile().getId())
                        .companyName(job.getRecruiterProfile().getCompanyName())
                        .companyLogoUrl(job.getRecruiterProfile().getCompanyLogoUrl())
                        .location(job.getRecruiterProfile().getLocation())
                        .build())
                .build();
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

}
