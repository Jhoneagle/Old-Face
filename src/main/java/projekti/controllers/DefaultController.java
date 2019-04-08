package projekti.controllers;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import projekti.models.Account;
import projekti.models.Friend;
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;

@Controller
public class DefaultController {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;
    
    private LocalDate time;
    
    @PostConstruct
    public void init() {
        time = LocalDate.now();
        Friend first = createFriend(0, time);
        this.friendRepository.save(first);
        first = this.friendRepository.findByTimestamp(time);

        Account sender = createAccount("admin", "admin");
        Account receiver = createAccount("owner", "owner");
        this.accountRepository.save(sender);
        this.accountRepository.save(receiver);
    }
    
    @GetMapping("*")
    public String helloWorld(Model model) {
        model.addAttribute("message", "World!");
        model.addAttribute("persons", this.accountRepository.findAll());
        return "index";
    }
    
    @PostMapping("/")
    public String associnate() {
        Friend first = this.friendRepository.findByTimestamp(time);

        List<Account> users = this.accountRepository.findAll();
        Account sender = users.get(0);
        Account receiver = users.get(1);

        first.setSender(sender);
        first.setReceiver(receiver);

        this.friendRepository.save(first);
        return "redirect:/";
    }
    
    public static Friend createFriend(long status, LocalDate time) {
        Friend first = new Friend();
        first.setStatus(status);
        first.setTimestamp(time);
        return first;
    }

    public static Account createAccount(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }
}
