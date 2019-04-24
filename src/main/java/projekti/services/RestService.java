package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.entities.Reaction;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.json.ReactionJson;
import projekti.domain.models.CommentModel;
import projekti.domain.json.FriendJson;
import projekti.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private StatusUpdateRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    public void createFriendRequest(FriendJson friendJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account sender = this.accountRepository.findByUsername(auth.getName());
        Account receiver = this.accountRepository.findByNickname(friendJson.getNickname());

        Friend exist = this.friendRepository.findBySenderAndReceiver(receiver, sender);
        Friend exist2 = this.friendRepository.findBySenderAndReceiver(sender, receiver);

        if (exist == null && exist2 == null) {
            Friend friend = new Friend();
            friend.setTimestamp(LocalDateTime.now());
            friend.setStatus((long) 0);
            friend.setSender(sender);
            friend.setReceiver(receiver);

            this.friendRepository.save(friend);
        }
    }

    public void cancelRequest(FriendJson friendJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account sender = this.accountRepository.findByUsername(auth.getName());
        Account receiver = this.accountRepository.findByNickname(friendJson.getNickname());

        Friend exist = this.friendRepository.findBySenderAndReceiver(receiver, sender);
        Friend exist2 = this.friendRepository.findBySenderAndReceiver(sender, receiver);

        if (exist == null) {
            this.friendRepository.delete(exist2);
        } else {
            this.friendRepository.delete(exist);
        }
    }

    public void handleRequest(FriendJson friendJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account receiver = this.accountRepository.findByUsername(auth.getName());
        Account sender = this.accountRepository.findByNickname(friendJson.getNickname());

        Friend friend = this.friendRepository.findBySenderAndReceiver(sender, receiver);

        if (friendJson.isAccept()) {
            friend.setStatus((long) 1);
        } else {
            friend.setStatus((long) -1);
        }

        this.friendRepository.save(friend);
    }

    public List<CommentModel> getCommentsOfPost(ReactionJson reactionJson) {
        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());
        List<Reaction> all = this.reactionRepository.findAllByStatusUpdateAndStatus(post, (long) 1);
        List<CommentModel> result = new ArrayList<>();

        for (Reaction a : all) {
            CommentModel model = new CommentModel();
            model.setCreator(a.getWho().getFullName());
            model.setNickname(a.getWho().getNickname());
            model.setContent(a.getContent());
            model.setTimestamp(a.getTimestamp());

            result.add(model);
        }

        return result;
    }

    public CommentModel createCommentForPost(ReactionJson reactionJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = this.accountRepository.findByUsername(auth.getName());

        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());
        Reaction result = new Reaction();
        result.setContent(reactionJson.getContent());
        result.setStatusUpdate(post);
        result.setTimestamp(LocalDateTime.now());
        result.setStatus((long) 1);
        result.setWho(user);

        result = this.reactionRepository.save(result);

        CommentModel model = new CommentModel();
        model.setContent(result.getContent());
        model.setNickname(result.getWho().getNickname());
        model.setCreator(result.getWho().getFullName());
        model.setTimestamp(result.getTimestamp());

        return model;
    }

    public void addLikeToPost(ReactionJson reactionJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = this.accountRepository.findByUsername(auth.getName());

        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());

        Reaction exist = this.reactionRepository.findByStatusUpdateAndStatusAndWho(post, (long) 0, user);

        if (exist == null) {
            Reaction result = new Reaction();
            result.setStatusUpdate(post);
            result.setTimestamp(LocalDateTime.now());
            result.setStatus((long) 0);
            result.setWho(user);

            this.reactionRepository.save(result);
        } else {
            this.reactionRepository.delete(exist);
        }
    }
}
