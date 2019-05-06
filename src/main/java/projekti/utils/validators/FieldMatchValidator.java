package projekti.utils.validators;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint class to handle the logic of validator annotation that has this connected into it.
 *
 * @see FieldMatch
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    /**
     * Initialize the fieldnames of the object that need to match with each other.
     * And message that will be given if the fields don't match.
     *
     * @param constraintAnnotation contains the parameters of the validator annotation.
     */
    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    /**
     * Validates if the fields in the object equals each other.
     *
     * @param value not sure exactly.
     * @param context holder object for context information of the validator and constraint classes.
     *
     * @return <code>true</code> if fields match with each other and <code>false</code> if they don't.
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean valid = true;

        try {
            final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
            final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

            valid =  firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        } catch (final Exception ignore) {
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
