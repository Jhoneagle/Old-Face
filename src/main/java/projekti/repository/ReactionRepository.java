package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

}
