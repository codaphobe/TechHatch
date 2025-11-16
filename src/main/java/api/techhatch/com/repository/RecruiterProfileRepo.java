package api.techhatch.com.repository;

import api.techhatch.com.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepo extends JpaRepository<RecruiterProfile, Long> {

    Optional<RecruiterProfile> findByUserId(Long userId);

    Optional<RecruiterProfile> findByUserEmail(String email);

    boolean existsByUserId(Long userId);
}
