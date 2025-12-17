package api.techhatch.com.exception;

public class OtpRateLimitExceededException extends RuntimeException {

    public OtpRateLimitExceededException(String message) {
        super(message);
    }
}
