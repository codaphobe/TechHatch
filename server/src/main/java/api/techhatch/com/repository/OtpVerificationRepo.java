package api.techhatch.com.repository;

import api.techhatch.com.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepo extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(
            String email, OtpVerification.OtpPurpose purpose);


    Optional<OtpVerification> findByOtpCodeAndEmail(String otpCode, String email);

    // Check if valid OTP exists
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM OtpVerification o " +
            "WHERE o.email = :email AND o.purpose = :purpose AND o.verified = false " +
            "AND o.expiresAt > :currentTime")
    boolean existsValidOtp(@Param("email") String email,
                           @Param("purpose") OtpVerification.OtpPurpose purpose,
                           @Param("currentTime") LocalDateTime currentTime);


    // Delete expired OTPs (cleanup job)
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :currentTime")
    void deleteExpiredOtp(@Param("currentTime") LocalDateTime currentTime);

    // Delete all OTPs for email (after successful verification)
    void deleteByEmail(String email);

}
