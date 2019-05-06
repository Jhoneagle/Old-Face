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

/**
 * Main controller as contains all the routes related to actions user can make after authentication that aren't called by javascript.
 * For REST API endpoints there is own specific controller to handle javascript related actions.
 *
 * @see AccountRelatedAPI
 */
@Controller
public class AccountRelatedController {
    @Autowired
    private MainService mainService;

    /**
     * Main page of the users which same time works as their personal wall where they and their friends can post status updates.
     * Also user can go the same users album or friend list.
     * Also in this page no matter whose it is you can see all friend requests you have gotten.
     *
     * @param model model object.
     * @param nickname nickname of the user whose wall wanted to see.
     *
     * @return template name.
     */
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

        // avoiding non fatal null pointer exception that is caused by 'fast access into lazy object'.
        // basically disabling the messages that are being spammed by this in the log otherwise.
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

    /**
     * Validates the status post form and returns the appropriate error messages or just saves the post and returns to previous page.
     * This router is also preAuthorized by security to make sure user has authorization to do this.
     *
     * @see StatusPostModel
     * @see projekti.utils.security.CustomPermissionEvaluator
     *
     * @param nickname nickname of the user whose wall have been.
     * @param statusPostModel validation model
     * @param bindingResult container for possible error messages
     * @param redirectAttributes object to hold all attributes that wanted to go ford when redirected to new page.
     *
     * @return redirection back to the page where came from.
     */
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

    /**
     * Returns search page showing there the result that was gotten by the parameter user gave.
     * In this situations the list of persons whose first and/or last name withs the search parameter.
     *
     * @param model model object
     * @param searchField search parameter
     * @return template name.
     */
    @PostMapping("/old-face/search")
    public String search(Model model, @RequestParam String searchField) {
        List<SearchResult> people = this.mainService.findPeopleWithParam(searchField);
        Map<String, ImageModel> pictures = this.mainService.getProfilePicturesForSearch(people);

        model.addAttribute("result", people);
        model.addAttribute("pictures", pictures);
        return "search-page";
    }

    /**
     * Shows list of persons that are being friend with the user whose list this is.
     *
     * @param model model object.
     * @param nickname nickname of the user whose friend list is being shown.
     * @return template name.
     */
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

    /**
     * Album page of an user where that users pictures are being shown.
     *
     * @param model model object.
     * @param nickname nickname of the user whose album is being shown.
     * @return template name.
     */
    @GetMapping("/old-face/{nickname}/album")
    public String albumPage(Model model, @PathVariable String nickname) {
        model.addAttribute("album", this.mainService.getPicturesInAlbum(nickname));
        model.addAttribute("profilePicture", this.mainService.getWallsProfilePicture(nickname));
        model.addAttribute("whoseWall", nickname);
        model.addAttribute("profileName", this.mainService.findByNickname(nickname).getFullName());
        return "album-page";
    }

    /**
     * Shows the specific picture of the album and gives links to possible previous and next image in the album besides what original album page gives.
     *
     * @see AccountRelatedController#albumPage(Model, String, Long)
     *
     * @param model model object.
     * @param nickname nickname of the user whose album is being shown.
     * @return template name.
     */
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

    /**
     * Creates new image into the database according to the data user gave in album pages form.
     * This router is also preAuthorized by security to make sure user has authorization to do this.
     *
     * @see projekti.utils.security.CustomPermissionEvaluator
     *
     * @param nickname nickname of the user whose album page user is at this moment.
     * @param content description of the image.
     * @param file image file.
     *
     * @return redirection to the main album page.
     */
    @PreAuthorize("hasPermission('albumOwner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/image")
    public String saveImage(@PathVariable String nickname, @RequestParam String content, @RequestParam MultipartFile file) {
        this.mainService.saveImage(file, content);
        return "redirect:/old-face/" + nickname + "/album";
    }

    /**
     * Deletes the image indicated by the id.
     * Returning to the original album page.
     * This router is also preAuthorized by security to make sure user has authorization to do this.
     *
     * @see projekti.utils.security.CustomPermissionEvaluator
     *
     * @param model model object.
     * @param nickname nickname of the user who owns the picture.
     * @param imageId id of the image selected.
     *
     * @return redirection to the main album page.
     */
    @PreAuthorize("hasPermission('owner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/{imageId}/del")
    public String deleteImage(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        this.mainService.deletePicture(imageId);
        return "redirect:/old-face/" + nickname + "/album";
    }

    /**
     * Sets the image indicated by the id to be new profile picture of users account.
     * Returning to the image page then.
     * This router is also preAuthorized by security to make sure user has authorization to do this.
     *
     * @see projekti.utils.security.CustomPermissionEvaluator
     *
     * @param model model object.
     * @param nickname nickname of the user who owns the picture.
     * @param imageId id of the image selected.
     *
     * @return redirection back to the image.
     */
    @PreAuthorize("hasPermission('owner', #nickname)")
    @PostMapping("/old-face/{nickname}/album/{imageId}/set")
    public String setAsProfilePicture(Model model, @PathVariable String nickname, @PathVariable Long imageId) {
        this.mainService.setAsProfilePicture(imageId);
        return "redirect:/old-face/" + nickname + "/album/" + imageId;
    }
}
