package projekti.domain.models.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * Validation object for wall posts form.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusPostModel {
    @NotEmpty
    private String content;
}
