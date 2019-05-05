package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageModel {
    private String fullName;
    private String description;
    private LocalDateTime timestamp;
    private Long status;
    private Long id;
    private Long likes;
    private boolean likedAlready;
}
