package projekti.domain.models.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureModel {
    @NotEmpty
    private String content;

    @NotEmpty
    private MultipartFile file;
}
