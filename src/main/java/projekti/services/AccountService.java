package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekti.models.Account;
import projekti.repository.AccountRepository;

import java.util.Map;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username);
    }

    public Account findByNickname(String nickname) {
        return this.accountRepository.findByNickname(nickname);
    }

    public String create(Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String passwordAgain = params.get("passwordAgain");
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String nickname = params.get("nickname");

        if (findByUsername(username) != null) return "username";

        String validate = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
        if (!password.matches(validate)) return "password";

        if (!passwordAgain.equals(password)) return "passwordAgain";

        if (findByNickname(nickname) != null) return "nickname";

        Account validated = new Account();
        validated.setUsername(username);
        validated.setPassword(encoder.encode(password));
        validated.setNickname(nickname);
        validated.setFirstName(firstName);
        validated.setLastName(lastName);

        this.accountRepository.save(validated);
        return "none";
    }
}
