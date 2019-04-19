package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Account;
import projekti.models.StatusUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StatusUpdateRepository extends JpaRepository<StatusUpdate, Long> {
    StatusUpdate findByTimestamp(LocalDateTime timestamp);

    @EntityGraph(attributePaths = {"image", "creator", "reactions"})
    List<StatusUpdate> findAllByTo(Account account);
}
