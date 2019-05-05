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
import projekti.domain.models.FriendModel;
import projekti.domain.models.ImageModel;
import projekti.domain.models.SearchResult;
import projekti.domain.models.WallPost;
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
        model.addAttribute("profilePicture", this.mainService.getWallsProfilePicture(nickname));
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "main-page";
    }

    @PreAuthorize("hasPermission('access', #nickname)")
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
        model.addAttribute("profilePicture", this.mainService.getWallsProfilePicture(nickname));
        model.addAttribute("friends", friends);
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", name);
        return "friend-page";
    }

    @GetMapping("/old-face/{nickname}/album")
    public String albumPage(Model model, @PathVariable String nickname) {
        model.addAttribute("album", this.mainService.getPicturesInAlbum(nickname));
        model.addAttribute("profilePicture", this.mainService.getWallsProfilePicture(nickname));
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", this.mainService.findByNickname(nickname).getFullName());
        return "album-page";
    }

    @GetMapping("/old-face/{nickname}/album/{imageId}")
    public String albumPage(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        model.addAttribute("current", this.mainService.getCurrentPicture(imageId));
        List<ImageModel> around = this.mainService.picturesAround(nickname, imageId);

        if (around.size() > 1) {
            model.addAttribute("previous", around.get(0));
            model.addAttribute("next", around.get(1));
        } else if (around.size() > 0) {
            if (around.get(0).getId() > imageId) {
                model.addAttribute("next", around.get(0));
            } else {
                model.addAttribute("previous", around.get(0));
            }
        }

        model.addAttribute("album", this.mainService.getPicturesInAlbum(nickname));
        model.addAttribute("profilePicture", this.mainService.getWallsProfilePicture(nickname));
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", this.mainService.findByNickname(nickname).getFullName());
        return "album-page";
    }

    @PreAuthorize("hasPermission('albumOwner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/image")
    public String saveImage(@PathVariable String nickname, @RequestParam String content, @RequestParam MultipartFile file) {
        this.mainService.saveImage(file, content);
        return "redirect:/old-face/" + nickname + "/album";
    }

    @PreAuthorize("hasPermission('owner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/{imageId}/del")
    public String deleteImage(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        this.mainService.deletePicture(imageId);
        return "redirect:/old-face/" + nickname + "/album";
    }

    @PreAuthorize("hasPermission('owner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/{imageId}/set")
    public String setAsProfilePicture(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        this.mainService.setAsProfilePicture(imageId);
        return "redirect:/old-face/" + nickname + "/album/" + imageId;
    }
}
