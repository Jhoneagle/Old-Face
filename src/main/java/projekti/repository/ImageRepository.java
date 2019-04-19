package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Account;
import projekti.models.Image;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @EntityGraph(attributePaths = {"owner", "content"})
    List<Image> findAllByStatusAndOwnerIn(Long status, Collection<Account> owner);
}
