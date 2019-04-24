package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel {
    private String content;
    private String creator;
    private String nickname;
    private LocalDateTime timestamp;
}
