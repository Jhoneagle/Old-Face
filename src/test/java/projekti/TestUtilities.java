package projekti;

import projekti.models.*;

import java.time.LocalDateTime;

public class TestUtilities {
    public static Friend createFriend(long status, LocalDateTime time) {
        Friend first = new Friend();
        first.setStatus(status);
        first.setTimestamp(time);
        return first;
    }

    public static Account createAccount(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }

    public static StatusUpdate createStatusUpdate(String content, LocalDateTime time) {
        StatusUpdate status = new StatusUpdate();
        status.setContent(content);
        status.setTimestamp(time);
        return status;
    }

    public static Image createImage(String filename, String description, String contentType, LocalDateTime time) {
        Image image = new Image();
        image.setTimestamp(time);
        image.setContentType(contentType);
        image.setDescription(description);
        image.setFilename(filename);
        image.setName(filename);
        return image;
    }

    public static Reaction createReaction(String content, Long status, LocalDateTime time) {
        Reaction reaction = new Reaction();
        reaction.setContent(content);
        reaction.setStatus(status);
        reaction.setTimestamp(time);
        return reaction;
    }
}
