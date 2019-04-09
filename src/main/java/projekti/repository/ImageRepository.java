package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Image;

import java.time.LocalDate;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByTimestamp(LocalDate timestamp);
}
