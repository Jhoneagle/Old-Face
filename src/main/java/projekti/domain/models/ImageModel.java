package projekti.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model object to safely represent a image for the user without having a risk that any unwanted information can be extracted.
 */
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
