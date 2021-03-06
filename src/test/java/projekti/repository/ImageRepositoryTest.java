package projekti.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import projekti.TestUtilities;
import projekti.domain.entities.Image;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ImageRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @After
    public void after() {
        this.imageRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleImage() {
        LocalDateTime time = LocalDateTime.now();
        Image image = TestUtilities.createImage("test.jpeg", "test image", "image/jpeg", time);
        this.imageRepository.save(image);

        image = this.imageRepository.findAll().get(0);
        assertNotNull(image);
        assertEquals("image/jpeg", image.getContentType());
        assertEquals("test.jpeg", image.getName());
    }

    @Test
    @Transactional
    public void addMultipleSimpleImage() {
        LocalDateTime time = LocalDateTime.now();
        Image image = TestUtilities.createImage("test.jpeg", "test image", "image/jpeg", time);
        this.imageRepository.save(image);

        image = TestUtilities.createImage("next.jpeg", "second of series", "image/jpeg", time);
        this.imageRepository.save(image);

        List<Image> all = this.imageRepository.findAll();
        assertEquals(2, all.size());
    }
}
