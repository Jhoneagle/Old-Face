package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Friend;

import java.time.LocalDate;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByTimestamp(LocalDate timestamp);
}
