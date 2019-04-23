package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekti.domain.entities.Account;
import projekti.domain.entities.Image;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.models.FriendModel;
import projekti.domain.models.SearchResult;
import projekti.domain.models.StatusPostModel;
import projekti.services.MainService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class AccountRelatedController {
    @Autowired
    private MainService mainService;

    @GetMapping("/old-face/{nickname}")
    public String mainPage(Model model, @PathVariable String nickname) {
        if (!model.containsAttribute("statusPostModel")) {
            model.addAttribute("statusPostModel", new StatusPostModel());
        }

        List<StatusUpdate> posts = this.mainService.getPosts(nickname);
        Account owner = this.mainService.findByNickname(nickname);
        List<Account> accounts = this.mainService.extractPeopleFromPosts(posts);
        List<FriendModel> friendRequests = this.mainService.getFriendRequests();

        if (!accounts.contains(owner)) {
            accounts.add(owner);
        }


        Map<String, Image> profilePictures = this.mainService.getAccountsProfilePictures(accounts);
        profilePictures.putAll(this.mainService.getFriendProfilePictures(friendRequests));
        String name = owner.getFirstName() + " " + owner.getLastName();

        model.addAttribute("requests", friendRequests);
        model.addAttribute("posts", posts);
        model.addAttribute("pictures", profilePictures);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "main-page";
    }

    @PreAuthorize("hasPermission('permission', #nickname)")
    @PostMapping("/old-face/{nickname}/postTo")
    public String addPost(Model model, @PathVariable String nickname, @Valid @ModelAttribute StatusPostModel statusPostModel,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.statusPostModel", bindingResult);
            redirectAttributes.addFlashAttribute("statusPostModel", statusPostModel);
            return "redirect:/old-face/" + nickname;
        }

        this.mainService.createPost(statusPostModel, nickname);
        return "redirect:/home";
    }

    @PostMapping("/old-face/search")
    public String search(Model model, @RequestParam String search) {
        List<SearchResult> people = this.mainService.findPeopleWithParam(search);
        Map<String, Image> pictures = this.mainService.getProfilePicturesForSearch(people);

        model.addAttribute("result", people);
        model.addAttribute("pictures", pictures);
        return "search-page";
    }

    @GetMapping("/old-face/{nickname}/friends")
    public String friendPage(Model model, @PathVariable String nickname) {
        Account owner = this.mainService.findByNickname(nickname);
        List<FriendModel> friends = this.mainService.getFriends(owner);
        String name = owner.getFirstName() + " " + owner.getLastName();
        Map<String, Image> pictures = this.mainService.getFriendProfilePictures(friends);

        model.addAttribute("pictures", pictures);
        model.addAttribute("friends", friends);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "friend-page";
    }

    @GetMapping("/old-face/{nickname}/album")
    public String albumPage(Model model, @PathVariable String nickname) {
        Account owner = this.mainService.findByNickname(nickname);
        String name = owner.getFirstName() + " " + owner.getLastName();

        

        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "album-page";
    }
}
