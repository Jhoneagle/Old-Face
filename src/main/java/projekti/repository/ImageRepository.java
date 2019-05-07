package projekti.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;
import projekti.domain.enums.PictureState;

import java.util.Collection;
import java.util.List;

@Transactional
public interface ImageRepository extends JpaRepository<Image, Long> {
    @EntityGraph(attributePaths = {"owner", "content"})
    List<Image> findAllByPictureStateAndOwnerIn(PictureState pictureState, Collection<Account> owner);

    Image findByOwnerAndPictureState(Account owner, PictureState pictureState);
    List<Image> findAllByOwner(Account owner);
}
