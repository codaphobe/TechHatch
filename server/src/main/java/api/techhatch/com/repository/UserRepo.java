package api.techhatch.com.repository;

import api.techhatch.com.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findUserByEmail(String email);
    Boolean existsByEmail(String email);
}