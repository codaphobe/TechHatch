package api.techhatch.com.repository;

import api.techhatch.com.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {

    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);

    Page<Job> findByRecruiterProfileId(Long recruiterId, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
            "j.status = 'ACTIVE' AND " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
            "(:minSalary IS NULL OR j.salaryMax >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.salaryMin <= :maxSalary)")
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") Job.JobType jobType,
            @Param("experienceLevel") Job.ExperienceLevel experienceLevel,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable
    );

}
