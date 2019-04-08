package projekti.controllers;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import projekti.models.Account;
import projekti.models.Friend;
import projekti.models.StatusUpdate;
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;
import projekti.repository.StatusUpdateRepository;

@Controller
public class DefaultController {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StatusUpdateRepository statusUpdateRepository;

    private LocalDate time;
    
    @PostConstruct
    public void init() {
        time = LocalDate.now();
        Friend first = createFriend(0, time);
        this.friendRepository.save(first);

        Account sender = createAccount("admin", "admin");
        Account receiver = createAccount("owner", "owner");
        this.accountRepository.save(sender);
        this.accountRepository.save(receiver);

        StatusUpdate status = createStatusUpdate("test status", time);
        this.statusUpdateRepository.save(status);
    }
    
    @GetMapping("*")
    public String helloWorld(Model model) {
        model.addAttribute("message", "World!");
        model.addAttribute("persons", this.accountRepository.findAll());
        return "index";
    }
    
    @PostMapping("/")
    public String associnate() {
        // Get Accounts
        List<Account> users = this.accountRepository.findAll();
        Account sender = users.get(0);
        Account receiver = users.get(1);

        // Account - Friend join
        Friend first = this.friendRepository.findByTimestamp(time);

        first.setSender(sender);
        first.setReceiver(receiver);

        this.friendRepository.save(first);

        // Account - StatusUpdate join
        StatusUpdate status = this.statusUpdateRepository.findByTimestamp(time);

        status.setCreator(sender);
        status.setTo(receiver);

        this.statusUpdateRepository.save(status);
        return "redirect:/";
    }
    
    private Friend createFriend(long status, LocalDate time) {
        Friend first = new Friend();
        first.setStatus(status);
        first.setTimestamp(time);
        return first;
    }

    private Account createAccount(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }

    private StatusUpdate createStatusUpdate(String content, LocalDate time) {
        StatusUpdate status = new StatusUpdate();
        status.setContent(content);
        status.setTimestamp(time);
        return status;
    }
}
