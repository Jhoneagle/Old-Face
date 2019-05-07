package projekti.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import projekti.services.AccountService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint class to handle the logic of validator annotation that has this connected into it.
 *
 * @see Nickname
 */
public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    @Autowired
    private AccountService accountService;

    /**
     * Checks from database if the nickname has been already taken and also checks if its preferred size.
     *
     * @param nickname value that is contained by the field in the object which is currently been validated that this annotation has been annotated.
     * @param constraintValidatorContext context stuff of the constraint and validator annotation. not that important.
     *
     * @return <code>true</code> if nickname hasn't been taken yet and otherwise <code>false</code>.
     */
    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        String violationMessage = "";

        if (nickname.isEmpty()) {
            violationMessage = "Nickname must not be empty!";
        } else if (nickname.length() < 4 || nickname.length() > 20) {
            violationMessage = "Nickname's length must be between 4-20 characters!";
        } else if (this.accountService.findByNickname(nickname) != null) {
            violationMessage = "Nickname has been already taken!";
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
