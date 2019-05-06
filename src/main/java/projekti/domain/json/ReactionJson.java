package projekti.domain.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container object to make javascript - REST API interaction more simple.
 * Main purpose to handle all he reacting related stuff like thumps up or down and comments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionJson {
    private Long id;
    private String content;
}
