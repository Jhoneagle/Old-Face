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
     * Checks from database if the nickname has been already taken.
     *
     * @param nickname value that is contained by the field in the object which is currently been validated that this annotation has been annotated.
     * @param constraintValidatorContext context stuff of the constraint and validator annotation. not that important.
     *
     * @return <code>true</code> if nickname hasn't been taken yet  and otherwise <code>false</code>.
     */
    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        if (nickname == null) {
            return false;
        }

        return accountService.findByNickname(nickname) == null;
    }

}
