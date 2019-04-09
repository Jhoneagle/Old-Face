package projekti.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reaction extends AbstractPersistable<Long> {
    private String content;
    private Long status;
    private LocalDate timestamp;

    @ManyToOne
    private Account who;

    @ManyToOne
    private Image image;

    @ManyToOne
    private StatusUpdate statusUpdate;
}
