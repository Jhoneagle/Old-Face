package projekti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;
import projekti.domain.entities.Reaction;
import projekti.domain.entities.StatusUpdate;

import java.util.Collection;
import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findAllByStatusUpdateAndStatus(StatusUpdate statusUpdate, Long status, Pageable pageable);
    Reaction findByStatusUpdateAndStatusAndWho(StatusUpdate statusUpdate, Long status, Account who);
    List<Reaction> findAllByImageAndStatus(Image statusUpdate, Long status, Pageable pageable);
    Reaction findByImageAndStatusAndWho(Image statusUpdate, Long status, Account who);
    void deleteAllByImage(Image image);
}
