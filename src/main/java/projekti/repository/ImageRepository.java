package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
