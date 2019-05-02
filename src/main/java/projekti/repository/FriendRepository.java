package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findBySenderAndReceiver(Account sender, Account receiver);
}
