package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import projekti.domain.enums.ReactionType;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Database table for all the reactions people create when they go through others posts and images.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reaction extends AbstractPersistable<Long> {
    private String content;
    private ReactionType reactionType;
    private LocalDateTime timestamp;

    @ManyToOne
    private Account who;

    // Applied if reaction is done to image.
    @ManyToOne
    private Image image;

    // Applied if reaction is done to post.
    @ManyToOne
    private StatusUpdate statusUpdate;
}
