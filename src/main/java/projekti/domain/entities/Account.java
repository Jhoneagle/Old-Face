package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Database User table. Containing all the info of the users.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AbstractPersistable<Long> {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String nickname;
    private String address;
    private String addressNumber;
    private String City;
    private String phoneNumber;
    private String email;
    private LocalDate born;

    // (account - friendshipState update - account) connection.
    @OneToMany(mappedBy = "creator")
    private List<StatusUpdate> posts = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    private List<StatusUpdate> wall = new ArrayList<>();

    // (account - friendship - account) connection.
    @OneToMany(mappedBy = "receiver")
    private List<Friend> receiverFriends = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Friend> sentFriends = new ArrayList<>();

    // Images the account has added.
    @OneToMany(mappedBy = "owner")
    private List<Image> images = new ArrayList<>();

    // Reactions account has made to posts and images.
    @OneToMany(mappedBy = "who")
    private List<Reaction> reactions = new ArrayList<>();

    // Authorities account has.
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities = new ArrayList<>();

    /**
     * Custom simplified tostring to prevent infinite loop because of the friendship and friendshipState update double users.
      */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Simplified way to get users full name without having it as one field still.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}