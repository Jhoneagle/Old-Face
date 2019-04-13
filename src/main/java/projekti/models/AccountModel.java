package projekti.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekti.utils.validators.FieldMatch;
import projekti.utils.validators.Nickname;
import projekti.utils.validators.Username;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "passwordAgain", message = "The password fields must match!")
public class AccountModel {
    @NotEmpty
    @Username
    private String username;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "Invalid password!")
    private String password;

    @NotEmpty
    private String passwordAgain;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    @Nickname
    private String nickname;
}