package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import projekti.services.MainService;

@Controller
public class AccountRelatedController {
    @Autowired
    private MainService mainService;

    @GetMapping("/old-face/{nickname}")
    public String helloWorld(Model model, @PathVariable String nickname) {
        model.addAttribute("username", this.mainService.findByNickname(nickname).getUsername());
        return "main-page";
    }
}
