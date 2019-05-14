package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projekti.domain.entities.Image;
import projekti.domain.json.FriendJson;
import projekti.domain.json.ReactionJson;
import projekti.domain.models.CommentModel;
import projekti.services.RestService;

import java.util.List;

/**
 * Controller to handle routes that only work as API endpoints for apps data.
 * And are related to interacting with others.
 */
@RestController
public class InteractAPIController {
    @Autowired
    private RestService restService;

    // Handlers for friend requests

    @PostMapping("/old-face/api/ask")
    public FriendJson askToBeFriend(@RequestBody FriendJson friendJson) {
        this.restService.createFriendRequest(friendJson);
        return friendJson;
    }

    @PostMapping("/old-face/api/ask/cancel")
    public FriendJson cancelRequest(@RequestBody FriendJson friendJson) {
        this.restService.cancelRequest(friendJson);
        return friendJson;
    }

    @PostMapping("/old-face/api/request")
    public FriendJson handleRequest(@RequestBody FriendJson friendJson) {
        this.restService.handleRequest(friendJson);
        return friendJson;
    }

    // Status post reactions

    @PostMapping("/old-face/api/post")
    public List<CommentModel> createCommentForPost(@RequestBody ReactionJson reactionJson) {
        return this.restService.createCommentForPost(reactionJson);
    }

    @PostMapping("/old-face/api/post/like")
    public ReactionJson likeOfThePost(@RequestBody ReactionJson reactionJson) {
        this.restService.addLikeToPost(reactionJson);
        return reactionJson;
    }

    @PostMapping("/old-face/api/post/get")
    public List<CommentModel> getCommentsOfPost(@RequestBody ReactionJson reactionJson) {
        return this.restService.getCommentsOfPost(reactionJson);
    }

    /**
     * Used to show images in the application without taking their byte arrays into template.
     * Can be used through image tags src attribute as if this was actual image file when in fact its gotten from database.
     *
     * @param id id of the image to be shown.
     * @return Content of the image.
     */
    @GetMapping("/old-face/api/files/{id}")
    public ResponseEntity<byte[]> viewImage(@PathVariable Long id) {
        Image image = this.restService.getImageById(id);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getContentType()));
        headers.setContentLength(image.getContentLength());
        headers.add("Content-Disposition", "attachment; filename=" + image.getFilename());

        return new ResponseEntity<>(image.getContent(), headers, HttpStatus.CREATED);
    }

    // Image reactions

    @PostMapping("/old-face/api/image")
    public List<CommentModel> createCommentForImage(@RequestBody ReactionJson reactionJson) {
        return this.restService.createCommentForImage(reactionJson);
    }

    @PostMapping("/old-face/api/image/like")
    public ReactionJson likeOfTheImage(@RequestBody ReactionJson reactionJson) {
        this.restService.addLikeToImage(reactionJson);
        return reactionJson;
    }

    @PostMapping("/old-face/api/image/get")
    public List<CommentModel> getCommentsOfImage(@RequestBody ReactionJson reactionJson) {
        return this.restService.getCommentsOfImage(reactionJson);
    }
}
