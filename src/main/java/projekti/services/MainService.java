package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.entities.Image;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.models.FriendModel;
import projekti.domain.models.SearchResult;
import projekti.domain.models.StatusPostModel;
import projekti.domain.models.WallPost;
import projekti.repository.AccountRepository;
import projekti.repository.ImageRepository;
import projekti.repository.StatusUpdateRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StatusUpdateRepository statusUpdateRepository;

    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username);
    }

    public Account findByNickname(String nickname) {
        return this.accountRepository.findByNickname(nickname);
    }

    public Map<String, Image> getAccountsProfilePictures(List<Account> accounts) {
        List<Image> raw = this.imageRepository.findAllByStatusAndOwnerIn(1L, accounts);
        return raw.stream().collect(Collectors.toMap(image -> image.getOwner().getUsername(), image -> image, (a, b) -> b));
    }

    public Map<String, Image> getProfilePicturesForSearch(List<SearchResult> searchresult) {
        List<String> nicknames = searchresult.stream().map(SearchResult::getNickname).collect(Collectors.toList());
        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    public Map<String, Image> getFriendProfilePictures(List<FriendModel> searchresult) {
        List<String> nicknames = searchresult.stream().map(FriendModel::getNickname).collect(Collectors.toList());
        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    public List<WallPost> getPosts(String nickname) {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("timestamp").descending());
        List<StatusUpdate> allByTo = this.statusUpdateRepository.findAllByTo(this.accountRepository.findByNickname(nickname), pageable);
        List<WallPost> result = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        allByTo.forEach(s -> {
            WallPost current = new WallPost();
            current.setContent(s.getContent());
            current.setFullname(s.getCreator().getFullName());
            current.setNickname(s.getCreator().getNickname());
            current.setTimestamp(s.getTimestamp());
            current.setId(s.getId());

            long isIt = s.getReactions().stream().filter(r -> r.getStatus() == 0 && r.getWho().getUsername().equals(auth.getName())).count();
            current.setLikedAlready(isIt == 1);

            long count = s.getReactions().stream().filter(r -> r.getStatus() == 0).count();
            current.setLikes(count);

            result.add(current);
        });

        return result;
    }

    public List<Account> extractPeopleFromPosts(List<WallPost> posts) {
        List<String> nicknames = posts.stream().map(WallPost::getNickname).collect(Collectors.toList());
        return this.accountRepository.findAllByNicknameIn(nicknames);
    }

    public void createPost(StatusPostModel statusPostModel, String nickname) {
        StatusUpdate post = new StatusUpdate();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        post.setContent(statusPostModel.getContent());
        post.setCreator(findByUsername(auth.getName()));
        post.setTo(findByNickname(nickname));
        post.setTimestamp(LocalDateTime.now());

        this.statusUpdateRepository.save(post);
    }

    public List<SearchResult> findPeopleWithParam(String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = findByUsername(auth.getName());

        String[] parts = search.split(" ");
        List<SearchResult> result = new ArrayList<>();
        List<Account> filtered;
        if (parts.length > 1) {
            StringBuilder firstName = new StringBuilder(parts[0]);
            String lastName = parts[parts.length - 1];

            for (int i = 1; i < parts.length - 1; i++) {
                firstName.append(" ").append(parts[i]);
            }

            filtered = this.accountRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstName.toString(), lastName);
        } else {
            String name = parts[0];

            filtered = this.accountRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
        }

        for (Account found : filtered) {
            SearchResult model = new SearchResult();
            model.setNickname(found.getNickname());
            model.setName(found.getFullName());

            model.setNotAsked(!found.getUsername().equals(user.getUsername()));

            for (Friend friend : found.getReceiverFriends()) {
                if (friend.getSender().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(false);
                    model.setRequest(friend.getStatus() == 0);
                    model.setBending(false);
                    break;
                }
            }

            for (Friend friend : found.getSentFriends()) {
                if (friend.getReceiver().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(false);
                    model.setRequest(friend.getStatus() == 0);
                    model.setBending(true);
                    break;
                }
            }

            result.add(model);
        }

        return result;
    }

    public List<FriendModel> getFriendRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = findByUsername(auth.getName());
        List<FriendModel> requests = new ArrayList<>();

        user.getReceiverFriends().stream().filter(a -> a.getStatus() == 0).forEach(a -> {
            FriendModel pal = new FriendModel();
            pal.setTimestamp(a.getTimestamp());
            pal.setNickname(a.getSender().getNickname());
            pal.setName(a.getSender().getFirstName() + " " + a.getSender().getLastName());

            requests.add(pal);
        });

        return requests;
    }

    public List<FriendModel> getFriends(Account person) {
        List<FriendModel> friends = new ArrayList<>();

        person.getReceiverFriends().stream().filter(a -> a.getStatus() > 0).forEach(a -> {
            FriendModel pal = new FriendModel();
            pal.setTimestamp(a.getTimestamp());
            pal.setNickname(a.getSender().getNickname());
            pal.setName(a.getSender().getFirstName() + " " + a.getSender().getLastName());

            friends.add(pal);
        });

        person.getSentFriends().stream().filter(a -> a.getStatus() > 0).forEach(a -> {
            FriendModel pal = new FriendModel();
            pal.setTimestamp(a.getTimestamp());
            pal.setNickname(a.getReceiver().getNickname());
            pal.setName(a.getReceiver().getFirstName() + " " + a.getReceiver().getLastName());

            friends.add(pal);
        });

        return friends;
    }
}
