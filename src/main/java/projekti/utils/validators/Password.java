package projekti.utils.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom validation annotation. Check if the password is valid or not.
 * Main logic is in Constraint class that is connected to this.
 * As this mainly handles only that it can be used as annotation.
 *
 * @see PasswordValidator
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Password {
    String message() default "Password is invalid!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}