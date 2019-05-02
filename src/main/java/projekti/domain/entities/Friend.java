package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend extends AbstractPersistable<Long> {
    private Long status;
    private LocalDateTime timestamp;

    @ManyToOne
    private Account sender;

    @ManyToOne
    private Account receiver;

    @Override
    public String toString() {
        return timestamp.toString();
    }
}