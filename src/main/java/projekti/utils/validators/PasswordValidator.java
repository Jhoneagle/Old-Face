package projekti.utils.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint class to handle the logic of validator annotation that has this connected into it.
 *
 * @see Password
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    /**
     * Checks if the password applies the following rules.
     * Must be between 8 and 20 characters.
     * Must have at least one upper case letter, one lower case letter and one number.
     * Can contain special characters.
     * Must not contain emoji nor spaces.
     *
     * @param password value that is contained by the field in the object which is currently been validated that this annotation has been annotated.
     * @param constraintValidatorContext context stuff of the constraint and validator annotation. not that important.
     *
     * @return <code>true</code> if password is valid and otherwise <code>false</code>.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        String violationMessage = ""; 
        
        if (password.isEmpty()) {
            violationMessage = "Password must not be empty!";
        } else if (password.length() < 8 || password.length() > 20) {
            violationMessage = "Passwords length must be between 8-20 characters!";
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$")) {
            violationMessage = "Invalid password!";
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
