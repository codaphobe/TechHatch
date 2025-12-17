package api.techhatch.com.service;

import api.techhatch.com.dto.request.LoginRequest;
import api.techhatch.com.dto.request.RegisterRequest;
import api.techhatch.com.dto.response.AuthResponse;
import api.techhatch.com.dto.response.OtpSentResponse;
import api.techhatch.com.dto.response.RegisterResponse;
import api.techhatch.com.exception.*;
import api.techhatch.com.model.*;
import api.techhatch.com.repository.PendingRegistrationRepo;
import api.techhatch.com.repository.UserRepo;
import api.techhatch.com.util.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import api.techhatch.com.model.Users.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final PendingRegistrationRepo pendingRegistrationRepo;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final OtpService otpService;

    public OtpSentResponse register(RegisterRequest request){

        if(userRepo.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email is already registered");
        }

        //check if there is a pending registration
        Optional<PendingRegistration> pendingUserRegistration = pendingRegistrationRepo.findByEmail(request.getEmail());
        //if the pending registration is expired delete
        pendingUserRegistration.ifPresent(p -> {
            if(p.isExpired()){
                pendingRegistrationRepo.delete(pendingUserRegistration.get());
                throw new UnauthorizedException("Registration expired. Please register again");
            }
        });

        PendingRegistration pendingRegistration;

        if(pendingUserRegistration.isPresent() && !pendingUserRegistration.get().isExpired()){
            pendingRegistration = pendingUserRegistration.get();

            otpService.checkRateLimit(pendingRegistration.getEmail());

            pendingRegistration.setPassword(encoder.encode(request.getPassword()));
            pendingRegistration.setExpiresAt(LocalDateTime.now().plusHours(24));
            pendingRegistration.setRole(Role.valueOf(request.getRole()));
            pendingRegistrationRepo.save(pendingRegistration);

        }else{
            final Role role;
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            }catch (IllegalArgumentException e){
                throw new UnauthorizedException("Invalid role");
            }


            pendingRegistration = PendingRegistration.builder()
                    .email(request.getEmail())
                    .password(encoder.encode(request.getPassword()))
                    .role(role)
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build();
            pendingRegistrationRepo.save(pendingRegistration);
        }

        //generate and send otp
        otpService.generateAndSendOtp(pendingRegistration.getEmail(), OtpVerification.OtpPurpose.REGISTRATION);

        log.info("OTP sent to {}, purpose {}", pendingRegistration.getEmail(), OtpVerification.OtpPurpose.REGISTRATION);

        //return response OtpSentResponse
        return OtpSentResponse.builder()
                .success(true)
                .message("Registration initiated. Please verify email with the otp sent to "+pendingRegistration.getEmail())
                .email(pendingRegistration.getEmail())
                .otpSent(true)
                .build();
    }

    public AuthResponse getCurrentUser(UserPrinciple userPrinciple){

        Users user  = userRepo.findUserByEmail(userPrinciple.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.builder()
                .userId(user.getId())
                .email(userPrinciple.getUsername())
                .role(user.getRole().toString())
                .message("User fetched successfully")
                .build();
    }


    public OtpSentResponse login(@Valid LoginRequest request) {

        if(isAuthenticated()){
            return OtpSentResponse.builder()
                    .success(false)
                    .email(request.getEmail())
                    .message("User is already authenticated")
                    .otpSent(false)
                    .build();
        }
        final Authentication authentication;
        try {
            authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        }catch (BadCredentialsException | DisabledException ex){
            throw new UnauthorizedException(ex.getMessage());
        }

        if(!authentication.isAuthenticated()){
            return OtpSentResponse.builder()
                    .success(false)
                    .email(request.getEmail())
                    .message("Login failed")
                    .error("Login failed")
                    .otpSent(false)
                    .build();
        }

        Users user = userRepo.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        //This check could be removed later, since every user is verified
        //`Users` entity is the source of truth
        //Non verified users are `PendingRegistration` user
        if (!user.getEmailVerified() || user.getAccountStatus() == Users.AccountStatus.UNVERIFIED){
            throw new UnauthorizedException("Verify email before logging in");
        }

        if (user.getAccountStatus() == Users.AccountStatus.SUSPENDED) {
            throw new ResourceNotFoundException("Account is suspended, try again after 15mins");
        }

        //check for rate limits
        otpService.checkRateLimit(request.getEmail());

        //generate and send otp for verification
        otpService.generateAndSendOtp(request.getEmail(), OtpVerification.OtpPurpose.LOGIN);

        log.info("OTP sent to {}, purpose {}", request.getEmail(), OtpVerification.OtpPurpose.LOGIN);

        return OtpSentResponse.builder()
                .success(true)
                .email(request.getEmail())
                .message("Login initiated. Please check your email for OTP")
                .otpSent(true)
                .build();
    }

    public RegisterResponse verifyUserRegistration(String email, String otpCode, OtpVerification.OtpPurpose otpPurpose){

        //if the user is already verified then return user
        Optional<Users> existingUser = userRepo.findUserByEmail(email);
        if(existingUser.isPresent()){
            return RegisterResponse.builder()
                    .success(true)
                    .email(existingUser.get().getEmail())
                    .role(existingUser.get().getRole().toString())
                    .userId(existingUser.get().getId())
                    .accountStatus(Users.AccountStatus.ACTIVE)
                    .verificationStatus(RegisterResponse.VerificationStatus.ALREADY_VERIFIED)
                    .message("Users verified already")
                    .build();
        }

        final PendingRegistration pendingRegistration = pendingRegistrationRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No pending registration found. Please register again"));


        if(pendingRegistration.isExpired()){
            pendingRegistrationRepo.delete(pendingRegistration);
            throw new UnauthorizedException("Registration expired. Please register again");
        }

        //verifies and marks the otp as consumed
        otpService.verifyOtp(otpCode,email,otpPurpose);

        final Role role;
        role = pendingRegistration.getRole();

        final Users users = Users.builder()
                .email(pendingRegistration.getEmail())
                .password(pendingRegistration.getPassword()) //already hashed the password
                .role(role)
                .emailVerified(true)
                .accountStatus(Users.AccountStatus.ACTIVE)
                .build();

        final Users savedUsers = userRepo.save(users);

        pendingRegistrationRepo.delete(pendingRegistration);

        return RegisterResponse.builder()
                .success(true)
                .email(savedUsers.getEmail())
                .role(savedUsers.getRole().toString())
                .userId(savedUsers.getId())
                .accountStatus(Users.AccountStatus.ACTIVE)
                .verificationStatus(RegisterResponse.VerificationStatus.VERIFIED)
                .message("Users verified and created Successfully")
                .build();
    }

    public AuthResponse verifyUserLogin(String email, String otpCode, OtpVerification.OtpPurpose otpPurpose){

        if(isAuthenticated()){
            return AuthResponse.builder()
                    .message("User is already authenticated")
                    .build();
        }

        Users user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. Please Register"));

        otpService.verifyOtp(otpCode, user.getEmail(), otpPurpose);

        //Generate token here
        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().toString(),user.getId());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().toString())
                .userId(user.getId())
                .message("Users Logged in Successfully")
                .build();
    }

    //check if the user is authenticated, if yes return instead of generating new otp/token
    private boolean isAuthenticated() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken);
    }
}
