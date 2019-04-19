package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Account;
import projekti.models.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByTimestamp(LocalDateTime timestamp);

    @EntityGraph(attributePaths = {"owner", "content"})
    List<Image> findAllByStatusAndOwnerIn(Long status, Collection<Account> owner);
}
