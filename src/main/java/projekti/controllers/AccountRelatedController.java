package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import projekti.models.Image;
import projekti.models.StatusPostModel;
import projekti.models.StatusUpdate;
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
        Map<String, Image> profilePictures = this.mainService.getAccountsProfilePictures(this.mainService.extractPeopleFromPosts(posts));

        model.addAttribute("posts", posts);
        model.addAttribute("pictures", profilePictures);
        model.addAttribute("whoseWall", nickname);
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
}
