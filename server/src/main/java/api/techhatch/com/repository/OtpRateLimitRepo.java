package api.techhatch.com.repository;

import api.techhatch.com.model.OtpRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRateLimitRepo extends JpaRepository<OtpRateLimit, Long> {

    Optional<OtpRateLimit> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM OtpRateLimit r WHERE r.windowStart < :cutoffTime")
    void deleteOldRecords(@Param("cutoffTime") LocalDateTime cutoffTime);
}
