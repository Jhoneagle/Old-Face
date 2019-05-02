package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekti.domain.json.FriendJson;
import projekti.domain.json.ReactionJson;
import projekti.domain.models.CommentModel;
import projekti.services.RestService;

import java.util.List;

@RestController
public class AccountRelatedAPI {
    @Autowired
    private RestService restService;

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
}
