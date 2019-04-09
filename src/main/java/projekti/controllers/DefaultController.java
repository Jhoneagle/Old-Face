package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.models.Account;
import projekti.repository.AccountRepository;

import java.util.Map;

@Controller
public class DefaultController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public String helloWorld(Model model) {
        model.addAttribute("message", "World!");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = this.accountRepository.findByUsername(auth.getName());
        return "redirect:/old-face/" + user.getNickname();
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@RequestParam Map<String, String> params) {

        return "redirect:/login";
    }
}
