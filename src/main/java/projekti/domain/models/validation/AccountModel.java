package projekti.domain.models.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekti.utils.validators.FieldMatch;
import projekti.utils.validators.Nickname;
import projekti.utils.validators.Username;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Validation object for registration form.
 *
 * @see FieldMatch
 * @see Username
 * @see Nickname
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

    // Demands at least one lower case, one upper case and one number character in the password.
    // Also demands password to be between 8 and 20 letters.
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "Invalid password!")
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