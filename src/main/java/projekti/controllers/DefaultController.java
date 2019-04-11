package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.services.AccountService;
import projekti.utils.ValidationException;

import java.util.Map;

@Controller
public class DefaultController {
    @Autowired
    private AccountService accountService;

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
        return "redirect:/old-face/" + this.accountService.findByUsername(auth.getName()).getNickname();
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@RequestParam Map<String, String> params) {
        try {
            this.accountService.create(params);
            return "redirect:/login";
        } catch (ValidationException exp) {
            return "redirect:/register?" + exp.getEffects();
        }
    }
}
