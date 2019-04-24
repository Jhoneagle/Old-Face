package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.models.FriendJson;
import projekti.repository.*;

import java.time.LocalDateTime;

@Service
@CrossOrigin("/**")
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
}
