package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {

}
