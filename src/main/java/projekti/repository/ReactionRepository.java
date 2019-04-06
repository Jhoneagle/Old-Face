package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

}
