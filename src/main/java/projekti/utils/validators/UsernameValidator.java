package projekti.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import projekti.services.AccountService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String> {
    @Autowired
    private AccountService accountService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        // null values are valid
        if (username == null ) {
            return true;
        }

        return accountService.findByUsername(username) == null;
    }

}
