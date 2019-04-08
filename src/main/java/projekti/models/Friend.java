package projekti.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend extends AbstractPersistable<Long> {
    //private String usernameOfWhoAsks;
    private Long status;
    private LocalDate timestamp;


    @ManyToOne
    private Account sender;

    @ManyToOne
    private Account receiver;
}