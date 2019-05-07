package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import projekti.domain.enums.FriendshipState;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Database friendship table.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend extends AbstractPersistable<Long> {
    private FriendshipState friendshipState = FriendshipState.NOT_REQUESTED;
    private LocalDateTime timestamp;

    @ManyToOne
    private Account sender;

    @ManyToOne
    private Account receiver;

    /**
     * Check Account table for this.
     *
     * @see Account#toString()
     */
    @Override
    public String toString() {
        return timestamp.toString();
    }
}