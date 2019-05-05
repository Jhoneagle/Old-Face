package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;

import java.util.Collection;
import java.util.List;

@Transactional
public interface ImageRepository extends JpaRepository<Image, Long> {
    @EntityGraph(attributePaths = {"owner", "content"})
    List<Image> findAllByStatusAndOwnerIn(Long status, Collection<Account> owner);
    Image findByOwnerAndStatus(Account owner, Long status);
    List<Image> findAllByOwner(Account owner);
}
