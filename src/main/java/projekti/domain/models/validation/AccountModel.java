package projekti.domain.models.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekti.utils.validators.FieldMatch;
import projekti.utils.validators.Nickname;
import projekti.utils.validators.Password;
import projekti.utils.validators.Username;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Validation object for registration form.
 *
 * @see FieldMatch
 * @see Username
 * @see Nickname
 * @see Password
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "passwordAgain", message = "The password fields must match!") // Checks if both fields match perfectly.
public class AccountModel {

    // Custom username check.
    @NotEmpty
    @Username
    private String username;

    // Custom password check.
    @NotEmpty
    @Password
    private String password;

    @NotEmpty
    private String passwordAgain;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    // Custom nickname check.
    @NotEmpty
    @Nickname
    private String nickname;
}