package api.techhatch.com.controller;

import api.techhatch.com.dto.request.VerifyOtpRequest;
import api.techhatch.com.dto.response.AuthResponse;
import api.techhatch.com.dto.response.OtpSentResponse;
import api.techhatch.com.dto.response.RegisterResponse;
import api.techhatch.com.model.OtpVerification;
import api.techhatch.com.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import api.techhatch.com.service.OtpService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final AuthService authService;

    @PostMapping("/verify-registration")
    public ResponseEntity<RegisterResponse> verifyRegistrationOtp(@RequestBody VerifyOtpRequest request) {

        RegisterResponse response = authService.verifyUserRegistration( request.getEmail(),request.getOtpCode(), request.getOtpPurpose());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/verify-login")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody VerifyOtpRequest request) {

        AuthResponse response = authService.verifyUserLogin(request.getEmail(),request.getOtpCode(), request.getOtpPurpose());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<OtpSentResponse> resendOtp(@RequestBody Map<String, String> body) {

        OtpSentResponse response = otpService.resendOtp(body.get("email"), OtpVerification.OtpPurpose.valueOf(body.get("otpPurpose")));
        return ResponseEntity.ok().body(response);
    }
}
