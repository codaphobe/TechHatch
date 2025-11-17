package api.techhatch.com.service;

import api.techhatch.com.dto.request.JobRequest;
import api.techhatch.com.dto.request.JobSearchFilter;
import api.techhatch.com.dto.response.JobResponse;
import api.techhatch.com.repository.JobRepo;
import api.techhatch.com.repository.RecruiterProfileRepo;
import api.techhatch.com.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final RecruiterProfileRepo recruiterRepo;
    private final UserRepo userRepo;
    private final JobRepo jobRepo;
    @Autowired
    private final ObjectMapper objectMapper;

    private static final int PAGE_SZE=15;

    public JobResponse postJob(String email, JobRequest request){

        return null;
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(JobSearchFilter filter){

        return null;
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id){

         return null;
    }

    /**
     * Get all jobs posted by recruiter
     * @return
     */
    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(String email, int page){
        return null;
    }

    public JobResponse updateJob(String email, Long id, JobRequest request){
        return null;
    }

    /**
     * Close the job, for recruiters only
     * @return
     */
    public JobResponse closeJob(String email, Long id){
        return null;
    }

    /**
     * Delete the job, for recruiters only
     * @return
     */
    public JobResponse deleteJob(String email, Long id){
        return null;
    }


}
