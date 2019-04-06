package projekti.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.StatusUpdate;

public interface WallRepository extends JpaRepository<StatusUpdate, Long> {

}
