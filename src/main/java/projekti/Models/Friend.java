package projekti.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend extends AbstractPersistable<Long> {
    //private String usernameOfWhoAsks;
    private Long status;
    private LocalDate timestamp;

    @ManyToOne
    private Account usernameOfWhoAsks;

    @ManyToOne
    private Account askedFrom;
}