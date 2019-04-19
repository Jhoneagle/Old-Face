package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Reaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByTimestamp(LocalDateTime timestamp);
}
