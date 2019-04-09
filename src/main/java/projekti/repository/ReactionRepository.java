package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Reaction;

import java.time.LocalDate;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByTimestamp(LocalDate timestamp);
}
