package projekti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.StatusUpdate;

import java.util.List;

public interface StatusUpdateRepository extends JpaRepository<StatusUpdate, Long> {
    @EntityGraph(attributePaths = {"image", "creator", "reactions"})
    List<StatusUpdate> findAllByTo(Account account, Pageable pageable);
}
