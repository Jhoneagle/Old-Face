package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.entities.Image;
import projekti.domain.entities.StatusUpdate;
import projekti.domain.enums.FriendshipState;
import projekti.domain.enums.PictureState;
import projekti.domain.enums.ReactionType;
import projekti.domain.models.FriendModel;
import projekti.domain.models.ImageModel;
import projekti.domain.models.SearchResult;
import projekti.domain.models.WallPost;
import projekti.domain.models.validation.StatusPostModel;
import projekti.repository.AccountRepository;
import projekti.repository.ImageRepository;
import projekti.repository.ReactionRepository;
import projekti.repository.StatusUpdateRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Main service class which contains methods that handle all the logic that is needed after person is authenticated.
 * Except those that are done through REST API endpoints. As they have own service class.
 *
 * @see RestService
 * @see projekti.controllers.AccountRelatedController
 */
@Service
public class MainService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StatusUpdateRepository statusUpdateRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    /**
     * Finds users account by his username.
     *
     * @param username username of account.
     *
     * @return account found.
     */
    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username);
    }

    /**
     * Finds users account by his nickname.
     *
     * @param nickname nickname of account.
     *
     * @return account found.
     */
    public Account findByNickname(String nickname) {
        return this.accountRepository.findByNickname(nickname);
    }

    /**
     * Get needed profile pictures of the accounts.
     * Gives map where key is accounts nickname and value is image model which is reformat version of image from database.
     *
     * @param accounts list of accounts that need their profile picture.
     *
     * @return map of string - image model pair.
     */
    public Map<String, ImageModel> getAccountsProfilePictures(List<Account> accounts) {
        List<Image> raw = this.imageRepository.findAllByPictureStateAndOwnerIn(PictureState.PROFILE_PICTURE, accounts);
        return raw.stream().collect(Collectors.toMap(image -> image.getOwner().getNickname(), this::formImageModel, (a, b) -> b));
    }

    /**
     * Get needed profile pictures of the accounts that are parsed from found persons in a search.
     * Gives map where key is accounts nickname and value is image model which is reformat version of image from database.
     *
     * @param searchresult list of persons found in search that need their profile picture.
     *
     * @return map of string - image model pair.
     */
    public Map<String, ImageModel> getProfilePicturesForSearch(List<SearchResult> searchresult) {
        List<String> nicknames = searchresult.stream().map(SearchResult::getNickname).collect(Collectors.toList());
        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    /**
     * Get needed profile pictures of the accounts that are parsed from found persons in a search.
     * Gives map where key is accounts nickname and value is image model which is reformat version of image from database.
     *
     * @param friendModelList list of friendships of persons that need their profile picture.
     *
     * @return map of string - image model pair.
     */
    public Map<String, ImageModel> getFriendProfilePictures(List<FriendModel> friendModelList) {
        List<String> nicknames = friendModelList.stream().map(FriendModel::getNickname).collect(Collectors.toList());
        return getAccountsProfilePictures(this.accountRepository.findAllByNicknameIn(nicknames));
    }

    /**
     * Gives list of posts in the wall of the person whose nickname the parameter is.
     * And between returning the list and database select call reformats the database entities into more secure model format.
     *
     * @param nickname nickname of the user whose posts in the wall needed.
     *
     * @return list of posts in the wall.
     */
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

            long isIt = s.getReactions().stream().filter(r -> r.getReactionType() == ReactionType.LIKE && r.getWho().getUsername().equals(auth.getName())).count();
            current.setLikedAlready(isIt == 1);
            long count = s.getReactions().stream().filter(r -> r.getReactionType() == ReactionType.LIKE).count();
            current.setLikes(count);

            result.add(current);
        });

        return result;
    }

    /**
     * Extract all accounts from wall posts who have created them.
     *
     * @param posts posts in the wall.
     *
     * @return list of people who made post into the specific wall.
     */
    public List<Account> extractPeopleFromPosts(List<WallPost> posts) {
        List<String> nicknames = posts.stream().map(WallPost::getNickname).collect(Collectors.toList());
        return this.accountRepository.findAllByNicknameIn(nicknames);
    }

    /**
     * Creates new post into the wall defined by nickname.
     *
     * @param statusPostModel forms validator object.
     * @param nickname nickname of the person whose wall post is created.
     */
    public void createPost(StatusPostModel statusPostModel, String nickname) {
        StatusUpdate post = new StatusUpdate();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        post.setContent(statusPostModel.getContent());
        post.setCreator(findByUsername(auth.getName()));
        post.setTo(findByNickname(nickname));
        post.setTimestamp(LocalDateTime.now());

        this.statusUpdateRepository.save(post);
    }

    /**
     * Looks for people whose first and/or last name contains the text given and
     * returns a list of those found ones in more useful and safer form.
     *
     * @param search user typing
     *
     * @return list of models that contain found people.
     */
    public List<SearchResult> findPeopleWithParam(String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = findByUsername(auth.getName());

        // Parse the parameter in the actual parameter and get the result as raw data from database.
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

        // Reformat the raw data into better format.
        for (Account found : filtered) {
            SearchResult model = new SearchResult();
            model.setNickname(found.getNickname());
            model.setName(found.getFullName());

            model.setNotAsked(!found.getUsername().equals(user.getUsername()));

            for (Friend friend : found.getReceiverFriends()) {
                if (friend.getSender().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(friend.getFriendshipState() == FriendshipState.DECLINED);
                    model.setRequest(friend.getFriendshipState() == FriendshipState.PENDING);
                    model.setBending(false);
                    break;
                }
            }

            for (Friend friend : found.getSentFriends()) {
                if (friend.getReceiver().getUsername().equals(user.getUsername())) {
                    model.setNotAsked(friend.getFriendshipState() == FriendshipState.DECLINED);
                    model.setRequest(friend.getFriendshipState() == FriendshipState.PENDING);
                    model.setBending(true);
                    break;
                }
            }

            result.add(model);
        }

        return result;
    }

    /**
     * Get all the friend requests what user has gotten.
     *
     * @return list of persons that have asked to be friend with the user itself.
     */
    public List<FriendModel> getFriendRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = findByUsername(auth.getName());
        return user.getReceiverFriends().stream().filter(a -> a.getFriendshipState() == FriendshipState.PENDING).map(a -> getFriendModel(a, a.getSender())).collect(Collectors.toList());
    }

    /**
     * Reformats database objects into secured versions that contains only needed info.
     * Account parameter is the who friend request was 'asked by us' or who asked 'us'.
     *
     * @param a friendship of the users.
     * @param notUserItself user who isn't the one asking friendships.
     *
     * @return reformat object to make database more saver.
     */
    private FriendModel getFriendModel(Friend a, Account notUserItself) {
        FriendModel pal = new FriendModel();
        pal.setTimestamp(a.getTimestamp());
        pal.setNickname(notUserItself.getNickname());
        pal.setName(notUserItself.getFirstName() + " " + notUserItself.getLastName());
        return pal;
    }

    /**
     * Get all friends of the person.
     *
     * @param person account whose friends needed.
     *
     * @return list of accounts who are friends with the account given as parameter.
     */
    public List<FriendModel> getFriends(Account person) {
        List<FriendModel> friends = person.getReceiverFriends().stream().filter(a -> a.getFriendshipState() == FriendshipState.ACCEPTED)
                .map(a -> getFriendModel(a, a.getSender())).collect(Collectors.toList());

        person.getSentFriends().stream().filter(a -> a.getFriendshipState() == FriendshipState.ACCEPTED)
                .map(a -> getFriendModel(a, a.getReceiver())).forEach(friends::add);

        return friends;
    }

    /**
     * Save image to database.
     *
     * @param file image file.
     * @param description content that is included to images data.
     */
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

        image.setPictureState(PictureState.PICTURE);
        image.setTimestamp(LocalDateTime.now());
        image.setDescription(description);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        image.setOwner(findByUsername(auth.getName()));

        this.imageRepository.save(image);
    }

    /**
     * Set wanted image as profile picture and before that update the old possible profile picture to not be profile picture.
     *
     * @param imageId id of the image.
     */
    public void setAsProfilePicture(Long imageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Image image = this.imageRepository.findByOwnerAndPictureState(findByUsername(auth.getName()), PictureState.PROFILE_PICTURE);

        if (image != null) {
            image.setPictureState(PictureState.PICTURE);
            this.imageRepository.save(image);
        }

        Image one = this.imageRepository.getOne(imageId);
        one.setPictureState(PictureState.PROFILE_PICTURE);
        this.imageRepository.save(one);
    }

    /**
     * get specific picture.
     *
     * @param imageId id of the image.
     *
     * @return image object from database that was asked.
     */
    public ImageModel getCurrentPicture(Long imageId) {
        Image one = this.imageRepository.getOne(imageId);
        return formImageModel(one);
    }

    /**
     * reformats image gotten from database to more ideal form so that no unwanted info is leaked to anyone.
     *
     * @param one image gotten from database.
     *
     * @return its reformat version as model.
     */
    private ImageModel formImageModel(Image one) {
        if (one == null) {
            return null;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ImageModel model = new ImageModel();
        model.setId(one.getId());
        model.setDescription(one.getDescription());
        model.setTimestamp(one.getTimestamp());
        model.setFullName(one.getOwner().getFullName());
        model.setPictureState(one.getPictureState());

        long isIt = one.getReactions().stream().filter(r -> r.getReactionType() == ReactionType.LIKE && r.getWho().getUsername().equals(auth.getName())).count();
        model.setLikedAlready(isIt == 1);
        long count = one.getReactions().stream().filter(r -> r.getReactionType() == ReactionType.LIKE).count();
        model.setLikes(count);
        return model;
    }

    /**
     * Returns users profile picture.
     *
     * @param nickname nickname of the user whose profile picture is needed.
     *
     * @return profile picture of user.
     */
    public ImageModel getWallsProfilePicture(String nickname) {
        Image image = this.imageRepository.findByOwnerAndPictureState(findByNickname(nickname), PictureState.PROFILE_PICTURE);
        return formImageModel(image);
    }

    /**
     * Returns all the images in the album of the specific user.
     *
     * @param nickname nickname of the user whose album is needed.
     *
     * @return lsit of images.
     */
    public List<ImageModel> getPicturesInAlbum(String nickname) {
        List<Image> images = this.imageRepository.findAllByOwner(findByNickname(nickname));
        return images.stream().map(this::formImageModel).collect(Collectors.toList());
    }

    /**
     * Deletes specified image and all reactions related to it.
     *
     * @param imageId id of the image.
     */
    @Transactional
    public void deletePicture(Long imageId) {
        Image one = this.imageRepository.getOne(imageId);
        this.reactionRepository.deleteAllByImage(one);
        this.imageRepository.delete(one);
    }

    /**
     * Tells if picture have another one before and/or after it and returns their model.
     *
     * @param nickname nickname of the user whose album is looked.
     * @param imageId id of the image currently selected.
     *
     * @return one or two image models.
     */
    public List<ImageModel> picturesAround(String nickname, Long imageId) {
        List<Image> images = this.imageRepository.findAllByOwner(findByNickname(nickname));
        List<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (Objects.equals(image.getId(), imageId)) {
                if ((i - 1) >= 0) {
                    ImageModel imageModel = formImageModel(images.get(i - 1));
                    list.add(imageModel);
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
