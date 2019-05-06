package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model object to safely represent a comment for the user without having a risk that any unwanted information can be extracted.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel {
    private String content;
    private String creator;
    private String nickname;
    private String timestamp;
}
