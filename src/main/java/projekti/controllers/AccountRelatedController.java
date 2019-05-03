package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekti.domain.entities.Account;
import projekti.domain.models.*;
import projekti.domain.models.validation.PictureModel;
import projekti.domain.models.validation.StatusPostModel;
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

        List<WallPost> posts = this.mainService.getPosts(nickname);
        Account owner = this.mainService.findByNickname(nickname);
        List<Account> accounts = this.mainService.extractPeopleFromPosts(posts);
        List<FriendModel> friendRequests = this.mainService.getFriendRequests();

        if (!accounts.contains(owner)) {
            accounts.add(owner);
        }


        Map<String, ImageModel> profilePictures = this.mainService.getAccountsProfilePictures(accounts);
        profilePictures.putAll(this.mainService.getFriendProfilePictures(friendRequests));
        String name;

        try {
            name = owner.getFullName();
        } catch(Exception e) {
            name = "";
        }

        model.addAttribute("requests", friendRequests);
        model.addAttribute("posts", posts);
        model.addAttribute("pictures", profilePictures);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "main-page";
    }

    @PreAuthorize("hasPermission('permission', #nickname)")
    @PostMapping("/old-face/{nickname}/postTo")
    public String addPost(@PathVariable String nickname, @Valid @ModelAttribute StatusPostModel statusPostModel,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.statusPostModel", bindingResult);
            redirectAttributes.addFlashAttribute("statusPostModel", statusPostModel);
        } else {
            this.mainService.createPost(statusPostModel, nickname);
        }

        return "redirect:/old-face/" + nickname;
    }

    @PostMapping("/old-face/search")
    public String search(Model model, @RequestParam String searchField) {
        List<SearchResult> people = this.mainService.findPeopleWithParam(searchField);
        Map<String, ImageModel> pictures = this.mainService.getProfilePicturesForSearch(people);

        model.addAttribute("result", people);
        model.addAttribute("pictures", pictures);
        return "search-page";
    }

    @GetMapping("/old-face/{nickname}/friends")
    public String friendPage(Model model, @PathVariable String nickname) {
        Account owner = this.mainService.findByNickname(nickname);
        List<FriendModel> friends = this.mainService.getFriends(owner);
        String name = owner.getFullName();
        Map<String, ImageModel> pictures = this.mainService.getFriendProfilePictures(friends);

        model.addAttribute("pictures", pictures);
        model.addAttribute("friends", friends);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "friend-page";
    }

    @GetMapping("/old-face/{nickname}/album")
    public String albumPage(Model model, @PathVariable String nickname) {
        if (!model.containsAttribute("pictureModel")) {
            model.addAttribute("pictureModel", new PictureModel());
        }

        Account owner = this.mainService.findByNickname(nickname);
        String name = owner.getFullName();



        model.addAttribute("profilePicture", null);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "album-page";
    }

    @GetMapping("/old-face/{nickname}/album/{imageId}")
    public String albumPage(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        if (!model.containsAttribute("pictureModel")) {
            model.addAttribute("pictureModel", new PictureModel());
        }

        Account owner = this.mainService.findByNickname(nickname);
        String name = owner.getFullName();

        

        model.addAttribute("profilePicture", null);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "album-page";
    }

    @PreAuthorize("hasPermission('authority', #nickname)")
    @PostMapping("/old-face/{nickname}/album/image")
    public String saveImage(@PathVariable String nickname, @Valid @ModelAttribute PictureModel pictureModel,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.pictureModel", bindingResult);
            redirectAttributes.addFlashAttribute("pictureModel", pictureModel);
        } else {
            this.mainService.save(pictureModel.getFile(), pictureModel.getContent());
        }

        return "redirect:/old-face/" + nickname + "/album";
    }
}
