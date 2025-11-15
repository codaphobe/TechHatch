package api.techhatch.com.repository;

import api.techhatch.com.model.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateProfileRepo extends JpaRepository<CandidateProfile, Long> {

    Optional<CandidateProfile> findByUserId(Long userId);

    Optional<CandidateProfile> findByUserEmail(String email);

    boolean existsByUserId(Long userId);
}
