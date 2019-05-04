package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.entities.Image;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.models.*;
import projekti.domain.models.validation.StatusPostModel;
import projekti.repository.AccountRepository;
import projekti.repository.ImageRepository;
import projekti.repository.StatusUpdateRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public Map<String, ImageModel> getAccountsProfilePictures(List<Account> accounts) {
        List<Image> raw = this.imageRepository.findAllByStatusAndOwnerIn(1L, accounts);
        return raw.stream().collect(Collectors.toMap(image -> image.getOwner().getNickname(), this::formImageModel, (a, b) -> b));
    }

    public Map<String, ImageModel> getProfilePicturesForSearch(List<SearchResult> searchresult) {
        List<String> nicknames = searchresult.stream().map(SearchResult::getNickname).collect(Collectors.toList());
        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    public Map<String, ImageModel> getFriendProfilePictures(List<FriendModel> searchresult) {
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

    public void saveImage(MultipartFile file, String description) {
        Image image = new Image();

        image.setFilename(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setContentLength(file.getSize());

        try {
            image.setContent(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        image.setStatus((long) 0);
        image.setTimestamp(LocalDateTime.now());
        image.setDescription(description);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        image.setOwner(findByUsername(auth.getName()));

        this.imageRepository.save(image);
    }

    public void setAsProfilePicture(Long imageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Image image = this.imageRepository.findByOwnerAndStatus(findByUsername(auth.getName()), (long) 1);

        if (image != null) {
            image.setStatus((long) 0);
            this.imageRepository.save(image);
        }

        Image one = this.imageRepository.getOne(imageId);
        one.setStatus((long) 1);
        this.imageRepository.save(one);
    }

    public ImageModel getCurrentPicture(Long imageId) {
        Image one = this.imageRepository.getOne(imageId);
        return formImageModel(one);
    }

    private ImageModel formImageModel(Image one) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ImageModel model = new ImageModel();
        model.setId(one.getId());
        model.setDescription(one.getDescription());
        model.setTimestamp(one.getTimestamp());
        model.setFullName(one.getOwner().getFullName());
        model.setStatus(one.getStatus());

        long isIt = one.getReactions().stream().filter(r -> r.getStatus() == 0 && r.getWho().getUsername().equals(auth.getName())).count();
        model.setLikedAlready(isIt == 1);
        long count = one.getReactions().stream().filter(r -> r.getStatus() == 0).count();
        model.setLikes(count);
        return model;
    }

    public ImageModel getWallsProfilePicture(String nickname) {
        Image image = this.imageRepository.findByOwnerAndStatus(findByNickname(nickname), (long) 1);
        return formImageModel(image);
    }

    public List<ImageModel> getPicturesInAlbum(String nickname) {
        List<Image> images = this.imageRepository.findAllByOwner(findByNickname(nickname));
        return images.stream().map(this::formImageModel).collect(Collectors.toList());
    }

    public void deletePicture(Long imageId) {
        Image one = this.imageRepository.getOne(imageId);
        this.imageRepository.delete(one);
    }

    public List<ImageModel> picturesAround(String nickname, Long imageId) {
        List<Image> images = this.imageRepository.findAllByOwner(findByNickname(nickname));
        List<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (Objects.equals(image.getId(), imageId)) {
                if ((i - 1) >= 0) {
                    ImageModel imageModel = formImageModel(images.get(i - 1));
                    list.add(imageModel);
                } else {
                    list.add(null);
                }

                if ((i + 1) < images.size()) {
                    ImageModel imageModel = formImageModel(images.get(i + 1));
                    list.add(imageModel);
                }
            }
        }

        return list;
    }
}
