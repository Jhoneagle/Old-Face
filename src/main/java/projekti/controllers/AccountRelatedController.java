package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projekti.models.*;
import projekti.services.MainService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class AccountRelatedController {
    @Autowired
    private MainService mainService;

    @GetMapping("/old-face/{nickname}")
    public String mainPage(Model model, @PathVariable String nickname, @ModelAttribute StatusPostModel statusPostModel) {
        return goToMainPage(model, nickname);
    }

    private String goToMainPage(Model model, @PathVariable String nickname) {
        List<StatusUpdate> posts = this.mainService.getPosts(nickname);
        Account owner = this.mainService.findByNickname(nickname);
        List<Account> accounts = this.mainService.extractPeopleFromPosts(posts);

        if (!accounts.contains(owner)) {
            accounts.add(owner);
        }

        Map<String, Image> profilePictures = this.mainService.getAccountsProfilePictures(accounts);

        model.addAttribute("posts", posts);
        model.addAttribute("pictures", profilePictures);
        model.addAttribute("whoseWall", owner.getNickname());
        model.addAttribute("profileName", owner.getFirstName() + " " + owner.getLastName());
        return "main-page";
    }

    @PreAuthorize("hasPermission('permission', #nickname)")
    @PostMapping("/old-face/{nickname}/postTo")
    public String addPost(Model model, @PathVariable String nickname, @Valid @ModelAttribute StatusPostModel statusPostModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return goToMainPage(model, nickname);
        }

        this.mainService.createPost(statusPostModel, nickname);
        return "redirect:/home";
    }

    @PostMapping("/old-face/search")
    public String search(Model model, @RequestParam String search) {
        List<SearchResult> people = this.mainService.findPeopleWithParam(search);
        model.addAttribute("result", people);
        model.addAttribute("pictures", this.mainService.getProfilePicturesForSearch(people));
        return "search-page";
    }

    @PostMapping("/old-face/ask")
    public String askToBeFriend(@RequestParam String nickname) {
        return "redirect:/home";
    }

    @GetMapping("/old-face/{nickname}/friends")
    public String friendPage(Model model, @PathVariable String nickname) {
        Account owner = this.mainService.findByNickname(nickname);



        model.addAttribute("whoseWall", owner.getNickname());
        model.addAttribute("profileName", owner.getFirstName() + " " + owner.getLastName());
        return "friend-page";
    }

    @GetMapping("/old-face/{nickname}/album")
    public String albumPage(Model model, @PathVariable String nickname) {
        Account owner = this.mainService.findByNickname(nickname);

        

        model.addAttribute("whoseWall", owner.getNickname());
        model.addAttribute("profileName", owner.getFirstName() + " " + owner.getLastName());
        return "album-page";
    }
}
