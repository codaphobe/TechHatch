package api.techhatch.com.service;

import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.model.Users;
import api.techhatch.com.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = repo.findUserByEmail(email);
        if(user.isPresent()){
            return new UserPrinciple(user.get());
        }
        throw new UsernameNotFoundException("User not found");
    }
}
