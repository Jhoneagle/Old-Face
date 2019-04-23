package projekti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.domain.entities.Account;

import java.util.Collection;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    Account findByNickname(String nickname);
    List<Account> findAllByFirstNameContainsOrLastNameContains(String firstName, String lastName);
    List<Account> findAllByNicknameIn(Collection<String> nickname);
}
