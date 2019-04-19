package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.models.Account;
import projekti.models.Friend;

import java.time.LocalDateTime;
import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByTimestamp(LocalDateTime timestamp);

    List<Friend> findAllBySenderAndReceiver(Account sender, Account receiver);
}
