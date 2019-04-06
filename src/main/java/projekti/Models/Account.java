package projekti.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "to")
    private List<StatusUpdate> wall;

    @OneToMany(mappedBy = "askedFrom")
    private List<Friend> friends;

    @OneToMany(mappedBy = "owner")
    private List<Image> images;

    @OneToMany(mappedBy = "who")
    private List<Reaction> reactions;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;
}