package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.models.*;
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;
import projekti.repository.ImageRepository;
import projekti.repository.StatusUpdateRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FriendRepository friendRepository;

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

        Map<String, Image> result = new HashMap<>();
        for (Image image : raw) {
            result.put(image.getOwner().getUsername(), image);
        }

        return result;
    }

    public Map<String, Image> getProfilePicturesForSearch(List<SearchResult> searchresult) {
        List<String> nicknames = new ArrayList<>();

        for (SearchResult s : searchresult) {
            nicknames.add(s.getNickname());
        }

        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    public List<StatusUpdate> getPosts(String nickname) {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("timestamp").descending());
        return this.statusUpdateRepository.findAllByTo(this.accountRepository.findByNickname(nickname), pageable);
    }

    public List<Account> extractPeopleFromPosts(List<StatusUpdate> posts) {
        List<Account> result = new ArrayList<>();

        for (StatusUpdate post : posts) {
            if (!result.contains(post.getCreator())) {
                result.add(post.getCreator());
            }
        }

        return result;
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

            filtered = this.accountRepository.findAllByFirstNameContainsOrLastNameContains(firstName.toString(), lastName);
        } else {
            String name = parts[0];

            filtered = this.accountRepository.findAllByFirstNameContainsOrLastNameContains(name, name);
        }

        for (Account found : filtered) {
            SearchResult model = new SearchResult();
            model.setNickname(found.getNickname());
            model.setName(found.getFirstName() + " " + found.getLastName());

            model.setNotAsked(!found.getUsername().equals(user.getUsername()));

            for (Friend friend : found.getReceiverFriends()) {
                if (friend.getSender().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(false);
                }
            }

            for (Friend friend : found.getSentFriends()) {
                if (friend.getReceiver().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(false);
                }
            }

            result.add(model);
        }

        return result;
    }
}
