package api.techhatch.com.repository;

import api.techhatch.com.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepo extends JpaRepository<JobApplication, Long> {

    boolean existsByJobIdAndCandidateProfileId(Long id, Long id1);


    //For candidates to find all applied jobs
    @Query(value = """
                    FROM JobApplication a WHERE
                    a.candidateProfile.id = :candidateId
                    AND (:statusFilter IS NULL OR a.status = :statusFilter)
                    """
    )
    Page<JobApplication> findCandidateApplications(@Param("candidateId") Long candidateId,
                                                   @Param("statusFilter") JobApplication.Status statusFilter,
                                                   Pageable pageable);

    @Query(value = """
            FROM JobApplication a WHERE
            a.job.id = :jobId
            AND (:statusFilter IS NULL OR a.status = :statusFilter)
            """)
    Page<JobApplication> findJobApplications(@Param("jobId") Long jobId,
                                             @Param("statusFilter")JobApplication.Status statusFilter,
                                             Pageable pageable);
}
