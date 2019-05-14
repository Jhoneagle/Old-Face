package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import projekti.domain.models.ImageModel;
import projekti.services.MainService;

import java.util.List;

/**
 * Controller to handel album routes which can be used by user after authentication and which ain't used through javascript.
 * For REST API endpoints there is own specific controller to handle javascript related actions.
 *
 * @see ProfileController
 * @see InteractAPIController
 */
@Controller
public class ImageController {
    @Autowired
    private MainService mainService;

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
     * @see ImageController#albumPage(Model, String, Long)
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
