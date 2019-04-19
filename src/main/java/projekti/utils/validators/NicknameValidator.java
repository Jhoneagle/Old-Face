package projekti.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import projekti.services.AccountService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    @Autowired
    private AccountService accountService;

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        if (nickname == null) {
            return false;
        }

        return accountService.findByNickname(nickname) == null;
    }

}
