package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.StatusUpdate;

import java.time.LocalDate;

public interface StatusUpdateRepository extends JpaRepository<StatusUpdate, Long> {
    StatusUpdate findByTimestamp(LocalDate timestamp);
}
