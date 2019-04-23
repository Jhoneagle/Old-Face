package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private String nickname;
    private String name;
    private boolean notAsked;
    private boolean request;
    private boolean bending;
}
