package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @EntityGraph(attributePaths = {"owner", "content"})
    List<Image> findAllByStatusAndOwnerIn(Long status, Collection<Account> owner);
}
