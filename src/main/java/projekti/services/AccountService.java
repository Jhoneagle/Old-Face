package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekti.models.Account;
import projekti.repository.AccountRepository;
import projekti.utils.ValidationException;

import java.util.Map;

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

    public void create(Map<String, String> params) throws ValidationException {
        String username = params.get("username");
        String password = params.get("password");
        String passwordAgain = params.get("passwordAgain");
        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String nickname = params.get("nickname");

        ValidationException exp = new ValidationException();
        // Validation
        if (findByUsername(username) != null) {
            if (exp.noEffects()) {
                exp.addEffect("usernameError");
            } else {
                exp.addEffect("&usernameError");
            }
        }

        String validate = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
        if (!password.matches(validate)) {
            if (exp.noEffects()) {
                exp.addEffect("passwordError");
            } else {
                exp.addEffect("&passwordError");
            }
        }

        if (!passwordAgain.equals(password)) {
            if (exp.noEffects()) {
                exp.addEffect("passwordAgainError");
            } else {
                exp.addEffect("&passwordAgainError");
            }
        }

        if (findByNickname(nickname) != null) {
            if (exp.noEffects()) {
                exp.addEffect("nicknameError");
            } else {
                exp.addEffect("&nicknameError");
            }
        }

        System.out.println("debug: " + exp.getEffects() + "!!!!!!!!");

        if(exp.noErrors()) {
            // Ff succeed then actions are made
            Account validated = new Account();
            validated.setUsername(username);
            validated.setPassword(password);// passwordEncoder.encode(password)
            validated.setNickname(nickname);
            validated.setFirstName(firstName);
            validated.setLastName(lastName);
            validated.getAuthorities().add("USER");

            this.accountRepository.save(validated);
        } else {
            throw exp;
        }
    }
}
