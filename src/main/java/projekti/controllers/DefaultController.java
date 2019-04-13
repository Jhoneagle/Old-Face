package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import projekti.models.AccountModel;
import projekti.services.AccountService;

import javax.validation.Valid;

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
    public String register(@ModelAttribute AccountModel accountModel) {
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@Valid @ModelAttribute AccountModel accountModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "register";
        }

        this.accountService.create(accountModel);
        return "redirect:/login";
    }
}
