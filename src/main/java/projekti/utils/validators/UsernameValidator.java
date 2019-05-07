package projekti.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import projekti.services.AccountService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Constraint class to handle the logic of validator annotation that has this connected into it.
 *
 * @see Username
 */
public class UsernameValidator implements ConstraintValidator<Username, String> {
    @Autowired
    private AccountService accountService;

    /**
     * Checks from database if the username has been already taken and also checks if its preferred size.
     *
     * @param username value that is contained by the field in the object which is currently been validated that this annotation has been annotated.
     * @param constraintValidatorContext context stuff of the constraint and validator annotation. not that important.
     *
     * @return <code>true</code> if username hasn't been taken yet and otherwise <code>false</code>.
     */
    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        String violationMessage = "";

        if (username.isEmpty()) {
            violationMessage = "Username must not be empty!";
        } else if (username.length() < 4 || username.length() > 20) {
            violationMessage = "Username's length must be between 4-20 characters!";
        } else if (this.accountService.findByUsername(username) != null) {
            violationMessage = "Username has been already taken!";
        }

        if (!(violationMessage.equals(""))) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(violationMessage)
                    .addConstraintViolation().disableDefaultConstraintViolation();
            return false;
        } else {
            return true;
        }
    }

}
