package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekti.domain.enums.FriendshipState;

/**
 * Model object to safely represent a search result (basically a person) for the user without having a risk that any unwanted information can be extracted.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private String nickname;
    private String name;
    private FriendshipState friendshipState;
}
