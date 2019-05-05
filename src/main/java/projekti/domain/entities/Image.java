package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image extends AbstractPersistable<Long> {
    private Long status;
    private String name;
    private String description;
    private String filename;
    private String contentType;
    private Long contentLength;
    private LocalDateTime timestamp;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    @ManyToOne
    private Account owner;

    @OneToOne
    private StatusUpdate update;

    @OneToMany(mappedBy = "image")
    private List<Reaction> reactions = new ArrayList<>();
}