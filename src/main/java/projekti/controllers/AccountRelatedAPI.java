package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projekti.services.RestService;

@RestController
public class AccountRelatedAPI {
    @Autowired
    private RestService restService;

    @PostMapping("/old-face/ask")
    public String askToBeFriend(@RequestParam String nickname) {
        //do something
        return "redirect:/home";
    }

    @PostMapping("/old-face/request/{nickname}/{accept}")
    public String acceptRequest(@PathVariable String nickname, @PathVariable Boolean accept) {
        //do something
        return "redirect:/home";
    }
}
