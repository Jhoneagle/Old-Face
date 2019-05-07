package projekti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;
import projekti.domain.entities.Reaction;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.enums.ReactionType;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findAllByStatusUpdateAndReactionType(StatusUpdate statusUpdate, ReactionType reactionType, Pageable pageable);
    Reaction findByStatusUpdateAndReactionTypeAndWho(StatusUpdate statusUpdate, ReactionType reactionType, Account who);
    List<Reaction> findAllByImageAndReactionType(Image statusUpdate, ReactionType reactionType, Pageable pageable);
    Reaction findByImageAndReactionTypeAndWho(Image image, ReactionType reactionType, Account who);
    void deleteAllByImage(Image image);
}
