package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.domain.entities.*;
import projekti.domain.enums.FriendshipState;
import projekti.domain.enums.ReactionType;
import projekti.domain.json.FriendJson;
import projekti.domain.json.ReactionJson;
import projekti.domain.models.CommentModel;
import projekti.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to contain all the logic what REST API controller needs.
 *
 * @see projekti.controllers.InteractAPIController
 */
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

    /**
     * creates bending request to be friends.
     *
     * @param friendJson JSON object gotten with API call.
     */
    public void createFriendRequest(FriendJson friendJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account sender = this.accountRepository.findByUsername(auth.getName());
        Account receiver = this.accountRepository.findByNickname(friendJson.getNickname());

        Friend exist = this.friendRepository.findBySenderAndReceiver(receiver, sender);
        Friend exist2 = this.friendRepository.findBySenderAndReceiver(sender, receiver);

        if (exist == null && exist2 == null) {
            Friend friend = new Friend();
            friend.setTimestamp(LocalDateTime.now());
            friend.setFriendshipState(FriendshipState.PENDING);
            friend.setSender(sender);
            friend.setReceiver(receiver);

            this.friendRepository.save(friend);
        } else {
            if (exist != null && exist.getFriendshipState() == FriendshipState.DECLINED) {
                exist.setFriendshipState(FriendshipState.PENDING);
                exist.setTimestamp(LocalDateTime.now());
                exist.setSender(sender);
                exist.setReceiver(receiver);

                this.friendRepository.save(exist);
            } else if (exist2 != null && exist2.getFriendshipState() == FriendshipState.DECLINED) {
                exist2.setFriendshipState(FriendshipState.PENDING);
                exist2.setTimestamp(LocalDateTime.now());
                exist2.setSender(sender);
                exist2.setReceiver(receiver);

                this.friendRepository.save(exist2);
            }
        }
    }

    /**
     * Cancels the request that has been created earlier but not yet accepted by the receiver.
     *
     * @param friendJson JSON object gotten with API call.
     */
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

    /**
     * Handle the friend request gotten from someone.
     *
     * @param friendJson JSON object gotten with API call.
     */
    public void handleRequest(FriendJson friendJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account receiver = this.accountRepository.findByUsername(auth.getName());
        Account sender = this.accountRepository.findByNickname(friendJson.getNickname());

        Friend friend = this.friendRepository.findBySenderAndReceiver(sender, receiver);

        if (friend == null) return;

        if (friendJson.isAccept()) {
            friend.setFriendshipState(FriendshipState.ACCEPTED);
        } else {
            friend.setFriendshipState(FriendshipState.DECLINED);
        }

        this.friendRepository.save(friend);
    }

    /**
     * Get all the comments of the post which id is given in the JSON.
     *
     * @param reactionJson JSON object gotten with API call.
     *
     * @return list of comments that have been given for the specific post.
     */
    public List<CommentModel> getCommentsOfPost(ReactionJson reactionJson) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());
        List<Reaction> all = this.reactionRepository.findAllByStatusUpdateAndReactionType(post, ReactionType.COMMENT, pageable);

        return createModelList(all);
    }

    /**
     *  Adds new comment for the post which id is in the JSON.
     *
     * @param reactionJson JSON object gotten with API call.
     *
     * @return list of comments that have been given for the specific post after adding new comment.
     */
    public List<CommentModel> createCommentForPost(ReactionJson reactionJson) {
        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(0, 9, Sort.by("timestamp").descending());
        List<Reaction> all = this.reactionRepository.findAllByStatusUpdateAndReactionType(post, ReactionType.COMMENT, pageable);
        Account user = this.accountRepository.findByUsername(auth.getName());

        Reaction result = new Reaction();
        result.setContent(reactionJson.getContent());
        result.setStatusUpdate(post);
        result.setTimestamp(LocalDateTime.now());
        result.setReactionType(ReactionType.COMMENT);
        result.setWho(user);

        this.reactionRepository.save(result);
        all.add(0, result);

        return createModelList(all);
    }

    /**
     * Reformats the list of comments in the more secure form.
     *
     * @param all comments to be reformat.
     *
     * @return reformated list of the same comments.
     */
    private List<CommentModel> createModelList(List<Reaction> all) {
        List<CommentModel> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (Reaction a : all) {
            CommentModel model = new CommentModel();
            model.setCreator(a.getWho().getFullName());
            model.setNickname(a.getWho().getNickname());
            model.setContent(a.getContent());
            model.setTimestamp(a.getTimestamp().format(formatter));

            result.add(model);
        }

        return result;
    }

    /**
     * Adds a like to the post.
     *
     * @param reactionJson JSON object gotten with API call.
     */
    public void addLikeToPost(ReactionJson reactionJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = this.accountRepository.findByUsername(auth.getName());

        StatusUpdate post = this.postRepository.getOne(reactionJson.getId());

        Reaction exist = this.reactionRepository.findByStatusUpdateAndReactionTypeAndWho(post, ReactionType.LIKE, user);

        if (exist == null) {
            Reaction result = new Reaction();
            result.setStatusUpdate(post);
            result.setTimestamp(LocalDateTime.now());
            result.setReactionType(ReactionType.LIKE);
            result.setWho(user);

            this.reactionRepository.save(result);
        } else {
            this.reactionRepository.delete(exist);
        }
    }

    /**
     * Finds image by its id.
     *
     * @param id id of an image.
     *
     * @return image that represents the id.
     */
    public Image getImageById(Long id) {
        return this.imageRepository.getOne(id);
    }

    /**
     * Get all the comments of the image which id is given in the JSON.
     *
     * @param reactionJson JSON object gotten with API call.
     *
     * @return list of comments that have been given for the specific post.
     */
    public List<CommentModel> getCommentsOfImage(ReactionJson reactionJson) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Image post = this.imageRepository.getOne(reactionJson.getId());
        List<Reaction> all = this.reactionRepository.findAllByImageAndReactionType(post, ReactionType.COMMENT, pageable);

        return createModelList(all);
    }

    /**
     *  Adds new comment for the image which id is in the JSON.
     *
     * @param reactionJson JSON object gotten with API call.
     *
     * @return list of comments that have been given for the specific post after adding new comment.
     */
    public List<CommentModel> createCommentForImage(ReactionJson reactionJson) {
        Image post = this.imageRepository.getOne(reactionJson.getId());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pageable pageable = PageRequest.of(0, 9, Sort.by("timestamp").descending());
        List<Reaction> all = this.reactionRepository.findAllByImageAndReactionType(post, ReactionType.COMMENT, pageable);
        Account user = this.accountRepository.findByUsername(auth.getName());

        Reaction result = new Reaction();
        result.setContent(reactionJson.getContent());
        result.setImage(post);
        result.setTimestamp(LocalDateTime.now());
        result.setReactionType(ReactionType.COMMENT);
        result.setWho(user);

        this.reactionRepository.save(result);
        all.add(0, result);

        return createModelList(all);
    }

    /**
     * Adds a like to the image.
     *
     * @param reactionJson JSON object gotten with API call.
     */
    public void addLikeToImage(ReactionJson reactionJson) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = this.accountRepository.findByUsername(auth.getName());

        Image post = this.imageRepository.getOne(reactionJson.getId());

        Reaction exist = this.reactionRepository.findByImageAndReactionTypeAndWho(post, ReactionType.LIKE, user);

        if (exist == null) {
            Reaction result = new Reaction();
            result.setImage(post);
            result.setTimestamp(LocalDateTime.now());
            result.setReactionType(ReactionType.LIKE);
            result.setWho(user);

            this.reactionRepository.save(result);
        } else {
            this.reactionRepository.delete(exist);
        }
    }
}
