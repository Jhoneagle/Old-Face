package projekti.models;

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

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdate extends AbstractPersistable<Long> {
    private String content;
    private LocalDateTime timestamp;

    @OneToOne
    private Image image;

    @ManyToOne
    private Account creator;

    @ManyToOne
    private Account to;

    @OneToMany(mappedBy = "statusUpdate")
    private List<Reaction> reactions = new ArrayList<>();

    @Override
    public String toString() {
        return content;
    }
}