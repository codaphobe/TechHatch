package api.techhatch.com.repository;

import api.techhatch.com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
//    Optional<User> findEmailById(String email);
    Boolean existsByEmail(String email);
}