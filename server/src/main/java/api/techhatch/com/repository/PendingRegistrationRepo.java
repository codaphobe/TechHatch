package api.techhatch.com.repository;

import api.techhatch.com.model.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingRegistrationRepo extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByEmail(String email);
}
