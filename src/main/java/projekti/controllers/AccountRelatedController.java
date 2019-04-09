package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import projekti.models.Account;
import projekti.repository.AccountRepository;

@Controller
public class AccountRelatedController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/old-face/{nickname}")
    public String helloWorld(Model model, @PathVariable String nickname) {
        Account byNickname = this.accountRepository.findByNickname(nickname);
        if(byNickname != null) model.addAttribute("username", byNickname.getUsername());

        return "main-page";
    }
}
