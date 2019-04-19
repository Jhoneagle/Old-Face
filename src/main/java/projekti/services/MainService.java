package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.models.Account;
import projekti.models.Image;
import projekti.models.StatusPostModel;
import projekti.models.StatusUpdate;
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;
import projekti.repository.ImageRepository;
import projekti.repository.StatusUpdateRepository;

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

    public List<StatusUpdate> getPosts(String nickname) {
        return this.statusUpdateRepository.findAllByTo(this.accountRepository.findByNickname(nickname));
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
}
