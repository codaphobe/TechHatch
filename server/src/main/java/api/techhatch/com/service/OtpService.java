package api.techhatch.com.service;

import api.techhatch.com.dto.response.OtpSentResponse;
import api.techhatch.com.exception.BadRequestException;
import api.techhatch.com.exception.OtpInvalidException;
import api.techhatch.com.exception.OtpRateLimitExceededException;
import api.techhatch.com.model.OtpRateLimit;
import api.techhatch.com.model.OtpVerification;
import api.techhatch.com.repository.OtpRateLimitRepo;
import api.techhatch.com.repository.OtpVerificationRepo;
import api.techhatch.com.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpVerificationRepo otpVerificationRepo;
    private final OtpRateLimitRepo otpRateLimitRepo;
    private final OtpMailService otpMailService;
    private final OtpAttemptsService attemptsService;

    private static final int MAX_REQUESTS=3;

    /**
     * Generate and send otp through mail
     * @param email receiver's email
     * @param otpPurpose purpose for new otp
     */
    public void generateAndSendOtp(String email, OtpVerification.OtpPurpose otpPurpose){

        invalidateExistingOtp(email, otpPurpose);

        //Context model for thymeleaf template
        final Context ctx = new Context();
        ctx.setVariable("appName", "TechHatch");
        String action = switch (otpPurpose) {
            case REGISTRATION -> "verify your email";
            case PASSWORD_RESET -> "reset your password";
            case LOGIN -> "login to your account";
        };
        ctx.setVariable("action", action);
        
        //check for exceeded attempts
        checkRateLimit(email);

        //check if a valid otp exists i.e. is not used and has not expired
        if(hasValidOtp(email, otpPurpose)){
            invalidateExistingOtp(email, otpPurpose);
        }

        String otpCode = OtpGenerator.generateOtp();
        LocalDateTime expiresAt = OtpGenerator.calculateExpiryTime();

        OtpVerification otpVerification = OtpVerification.builder()
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .email(email)
                .purpose(otpPurpose)
                .verificationAttempts(0)
                .maxAttempts(3)
                .verified(false)
                .build();

        otpVerificationRepo.save(otpVerification);

        updateRateLimit(email);

        ctx.setVariable("otp", otpVerification.getOtpCode());
        otpMailService.sendOtpEmail(email, ctx, otpPurpose);
    }

    public boolean verifyOtp(String otpCode , String email, OtpVerification.OtpPurpose otpPurpose){

        OtpVerification latestOtp = otpVerificationRepo.findTopByEmailAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(email, otpPurpose)
                .orElseThrow(() -> new OtpInvalidException("No OTP found for this email"));

        //check if otp is used
        if(latestOtp.isVerified()){
            throw new OtpInvalidException("OTP already used");
        }

        //check if otp is expired
        if(latestOtp.isExpired()){
            throw new OtpInvalidException("OTP expired, Please request a new one");
        }

        //check if verification attempts exceeded
        if(latestOtp.hasExceededAttempts()){
            throw new OtpInvalidException("Maximum attempts exceeded please try again later");
        }

        //verify otp
        if(!otpCode.equals(latestOtp.getOtpCode())){
            attemptsService.recordFailedAttempts(latestOtp);

            int remainingAttempts = latestOtp.getMaxAttempts() - latestOtp.getVerificationAttempts();
            throw new OtpInvalidException("Invalid OTP. "+remainingAttempts+" attempts remaining");
        }

        //mark as verified
        latestOtp.setVerified(true);
        latestOtp.setVerifiedAt(LocalDateTime.now());
        otpVerificationRepo.save(latestOtp);

        log.info("Otp verified successfully for email: {}", email);
        return true;
    }

    //update the request count on the rate limits
    private void updateRateLimit(String email) {

        Optional<OtpRateLimit> otpRateLimit = otpRateLimitRepo.findByEmail(email);

        if(otpRateLimit.isPresent()){
            OtpRateLimit rateLimit = otpRateLimit.get();
            rateLimit.setRequestCount(rateLimit.getRequestCount() + 1);
            otpRateLimitRepo.save(rateLimit);
        }else {
            OtpRateLimit rateLimit = OtpRateLimit.builder()
                    .email(email)
                    .requestCount(1)
                    .windowStart(LocalDateTime.now())
                    .build();
            otpRateLimitRepo.save(rateLimit);
        }
    }

    /**
     * Checks if the user has exceeded rate limits
     */
    public void checkRateLimit(String email) {

        Optional<OtpRateLimit> otpRateLimit = otpRateLimitRepo.findByEmail(email);
        if (otpRateLimit.isEmpty()){
            return;
        }

        final OtpRateLimit rateLimit = otpRateLimit.get();

        //check if the user is blocked
        if(rateLimit.isBlocked()){
            long minsLeft = Duration.between(LocalDateTime.now(), rateLimit.getBlockedUntil()).toMinutes();
            throw new OtpRateLimitExceededException("Too many OTP requests. Please try again in " + minsLeft + " minutes");
        }

        //check if the window is expired
        if(rateLimit.isWindowExpired()){
            rateLimit.setWindowStart(LocalDateTime.now());
            rateLimit.setRequestCount(0);
            otpRateLimitRepo.save(rateLimit);
            return;
        }

        //check if the limit has exceeded (3 tries)
        if(rateLimit.getRequestCount()>=MAX_REQUESTS){
            rateLimit.setBlockedUntil(LocalDateTime.now().plusMinutes(15));
            otpRateLimitRepo.save(rateLimit);
            throw new OtpRateLimitExceededException(
                    "Maximum OTP requests exceeded. Please try again in 15 minutes"
            );
        }
    }

    /**
     * Resends otp to the provided email, uses `generateAndSendOtp`
     */
    public OtpSentResponse resendOtp(String email, OtpVerification.OtpPurpose otpPurpose){
        try{
            generateAndSendOtp(email, otpPurpose);
            return OtpSentResponse.builder()
                    .success(true)
                    .otpSent(true)
                    .message("A new OTP has been sent to the provided email")
                    .email(email)
                    .build();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    private boolean hasValidOtp(String email, OtpVerification.OtpPurpose otpPurpose){
        return otpVerificationRepo.existsValidOtp(email, otpPurpose, LocalDateTime.now());
    }

    private void invalidateExistingOtp(String email, OtpVerification.OtpPurpose otpPurpose){
        Optional<OtpVerification> existingOtp = otpVerificationRepo.findTopByEmailAndPurposeAndVerifiedFalseOrderByCreatedAtDesc(email, otpPurpose);
        existingOtp.ifPresent( otp -> {
            otp.setVerified(true);
            otpVerificationRepo.save(existingOtp.get());
        });
    }

}
