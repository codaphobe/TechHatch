package api.techhatch.com.service;

import api.techhatch.com.dto.request.RegisterRequest;
import api.techhatch.com.dto.response.AuthResponse;
import api.techhatch.com.model.User;
import api.techhatch.com.repository.UserRepo;
import api.techhatch.com.util.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import api.techhatch.com.model.User.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JWTService jwtService;
    private final UserRepo repo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public AuthResponse register(RegisterRequest request){
        if(repo.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Invalid role.");
        }

        User user  = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        User savedUser = repo.save(user);

        //TODO:Generate token here


        //return response AuthResponse
        return AuthResponse.builder()
                .token(jwtService.generateToken()) //a dummy token
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .userId(savedUser.getId())
                .message("User created Successfully")
                .build();

    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }


}
