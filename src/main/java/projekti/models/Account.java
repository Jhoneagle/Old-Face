package projekti.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "creator")
    private List<StatusUpdate> posts = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    private List<StatusUpdate> wall = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Friend> receiverFriends = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Friend> sentFriends = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "who")
    private List<Reaction> reactions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities = new ArrayList<>();
}