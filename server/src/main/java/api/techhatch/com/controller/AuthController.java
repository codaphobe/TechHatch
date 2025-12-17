package api.techhatch.com.controller;

import api.techhatch.com.dto.request.LoginRequest;
import api.techhatch.com.dto.request.RegisterRequest;
import api.techhatch.com.dto.response.AuthResponse;
import api.techhatch.com.dto.response.OtpSentResponse;
import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<OtpSentResponse> registerUser(@RequestBody @Valid RegisterRequest request){

            final OtpSentResponse response = service.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<OtpSentResponse> loginUser(@RequestBody @Valid LoginRequest request){

        OtpSentResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal UserPrinciple userPrinciple){

        AuthResponse response = service.getCurrentUser(userPrinciple);
        return ResponseEntity.ok(response);
    }
}
