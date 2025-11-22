package api.techhatch.com.service;

import api.techhatch.com.dto.request.LoginRequest;
import api.techhatch.com.dto.request.RegisterRequest;
import api.techhatch.com.dto.response.AuthResponse;
import api.techhatch.com.dto.response.RegisterResponse;
import api.techhatch.com.model.Users;
import api.techhatch.com.repository.UserRepo;
import api.techhatch.com.util.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import api.techhatch.com.model.Users.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepo repo;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authManager;

    public RegisterResponse register(RegisterRequest request){
        if(repo.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Invalid role.");
        }

        Users users = Users.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        Users savedUsers = repo.save(users);

        //return response RegisterResponse
        return RegisterResponse.builder()
                .email(savedUsers.getEmail())
                .role(savedUsers.getRole().toString())
                .userId(savedUsers.getId())
                .message("Users created Successfully")
                .build();
    }

    public List<Users> getAllUsers(){
        return repo.findAll();
    }


    public AuthResponse login(@Valid LoginRequest request) {

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        if(!authentication.isAuthenticated()){
            return AuthResponse.builder()
                    .message("Login Failed")
                    .build();
        }

        Users user = repo.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        //Generate token here
        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().toString(),user.getId());

        return AuthResponse.builder()
                .token(token)
                .email(request.getEmail())
                .role(user.getRole().toString())
                .userId(user.getId())
                .message("Users Logged in Successfully")
                .build();

    }
}
