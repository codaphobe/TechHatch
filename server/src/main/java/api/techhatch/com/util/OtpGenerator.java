package api.techhatch.com.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
public class OtpGenerator {

    private static final int OTP_LENGTH = 6;
    private static final int EXPIRY_TIME = 10; //10 mins
    private static final int MAX = (int) Math.pow(10,OTP_LENGTH);
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a 6-digit numeric OTP
     * @return OTP as string (e.g., "012345")
     */
    public static String generateOtp() {
        int otp = random.nextInt(MAX); // 0 to 999999 - 6 digit
        return String.format("%06d", otp); // Pad with leading zeros
    }

    /**
     * Calculate expiry time (current time + minutes)
     * @return expiry timestamp
     */
    public static LocalDateTime calculateExpiryTime() {
        return LocalDateTime.now().plusMinutes(EXPIRY_TIME);
    }
}
