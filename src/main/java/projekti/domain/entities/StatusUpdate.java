package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database table for the post people can make into their own and their friends walls.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdate extends AbstractPersistable<Long> {
    private String content;
    private LocalDateTime timestamp;

    // Possibility to connect image into post.
    // NOT YET IMPLEMENTED!!!
    @OneToOne
    private Image image;

    @ManyToOne
    private Account creator;

    @ManyToOne
    private Account to;

    // Reactions related to this post.
    @OneToMany(mappedBy = "statusUpdate")
    private List<Reaction> reactions = new ArrayList<>();

    /**
     * Check Account table for this.
     *
     * @see Account#toString()
     */
    @Override
    public String toString() {
        return content;
    }
}