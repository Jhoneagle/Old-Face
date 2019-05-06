package projekti.utils.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom validation annotation. Check if the username is already taken or not.
 * Main logic is in Constraint class that is connected to this.
 * As this mainly handles only that it can be used as annotation.
 *
 * @see UsernameValidator
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(Username.List.class)
@Documented
@Constraint(validatedBy = { UsernameValidator.class })
public @interface Username {
    String message() default "Username must be unique!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Username[] value();
    }
}
