package projekti.domain.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container object to make javascript - REST API interaction more simple.
 * Main purpose to handle people related things. So basically friend requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendJson {
    private String nickname;
    private boolean accept;
}
