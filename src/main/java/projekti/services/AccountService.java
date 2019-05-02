package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekti.domain.entities.Account;
import projekti.domain.models.AccountModel;
import projekti.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username);
    }

    public Account findByNickname(String nickname) {
        return this.accountRepository.findByNickname(nickname);
    }

    public void create(AccountModel account) {
        Account validated = new Account();
        validated.setUsername(account.getUsername());
        validated.setPassword(passwordEncoder.encode(account.getPassword()));
        validated.setNickname(account.getNickname());
        validated.setFirstName(account.getFirstName());
        validated.setLastName(account.getLastName());
        validated.getAuthorities().add("USER");

        this.accountRepository.save(validated);
    }
}
