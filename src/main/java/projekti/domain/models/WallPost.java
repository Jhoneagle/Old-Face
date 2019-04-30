package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WallPost {
    private String fullname;
    private String nickname;
    private String content;
    private LocalDateTime timestamp;
    private Long likes;
    private Long id;
    private boolean likedAlready;
}
