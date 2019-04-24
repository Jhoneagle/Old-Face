package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projekti.domain.models.FriendJson;
import projekti.services.RestService;

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
}
