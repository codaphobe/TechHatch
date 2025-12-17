package api.techhatch.com.service;

import api.techhatch.com.model.OtpVerification;
import api.techhatch.com.repository.OtpVerificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OtpAttemptsService {

    private final OtpVerificationRepo otpVerificationRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempts(OtpVerification latestOtp){
        latestOtp.incrementAttempts();
        otpVerificationRepo.save(latestOtp);
    }
}
