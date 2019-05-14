package projekti.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;
import projekti.domain.enums.PictureState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database image table. Contains all the images in the application that users are posting.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image extends AbstractPersistable<Long> {
    private PictureState pictureState;
    private String name;
    private String description;
    private LocalDateTime timestamp;

    // Image headers.
    private String filename;
    private String contentType;
    private Long contentLength;

    // Actual image or well more like its digital representation.
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    @ManyToOne
    private Account owner;

    // NOT YET IMPLEMENTED!!!
    @OneToOne
    private StatusUpdate update;

    // Reactions related to this image.
    @OneToMany(mappedBy = "image")
    private List<Reaction> reactions = new ArrayList<>();
}