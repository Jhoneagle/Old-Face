package projekti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.Reaction;
import projekti.domain.entities.StatusUpdate;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findAllByStatusUpdateAndStatus(StatusUpdate statusUpdate, Long status, Pageable pageable);
    Reaction findByStatusUpdateAndStatusAndWho(StatusUpdate statusUpdate, Long status, Account who);
}
