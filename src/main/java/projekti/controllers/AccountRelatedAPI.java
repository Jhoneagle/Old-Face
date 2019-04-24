package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import projekti.domain.json.FriendJson;
import projekti.domain.json.ReactionJson;
import projekti.domain.models.CommentModel;
import projekti.services.RestService;

import java.util.List;

@RestController
public class AccountRelatedAPI {
    @Autowired
    private RestService restService;

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/ask")
    public FriendJson askToBeFriend(@RequestBody FriendJson friendJson) {
        this.restService.createFriendRequest(friendJson);
        return friendJson;
    }

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/ask/cancel")
    public FriendJson cancelRequest(@RequestBody FriendJson friendJson) {
        this.restService.cancelRequest(friendJson);
        return friendJson;
    }

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/request")
    public FriendJson handleRequest(@RequestBody FriendJson friendJson) {
        this.restService.handleRequest(friendJson);
        return friendJson;
    }

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/post")
    public CommentModel createCommentForPost(@RequestBody ReactionJson reactionJson) {
        return this.restService.createCommentForPost(reactionJson);
    }

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/post/like")
    public ReactionJson likeOfThePost(@RequestBody ReactionJson reactionJson) {
        this.restService.addLikeToPost(reactionJson);
        return reactionJson;
    }

    @CrossOrigin(origins = "/**")
    @PostMapping("/old-face/api/post")
    public List<CommentModel> getCommentsOfPost(@RequestBody ReactionJson reactionJson) {
        return this.restService.getCommentsOfPost(reactionJson);
    }
}
