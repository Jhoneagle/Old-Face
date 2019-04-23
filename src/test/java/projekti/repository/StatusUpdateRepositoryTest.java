package projekti.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import projekti.TestUtilities;
import projekti.domain.entities.StatusUpdate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class StatusUpdateRepositoryTest {
    @Autowired
    private StatusUpdateRepository statusUpdateRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Before
    public void before() {
        this.statusUpdateRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleStatusUpdate() {
        LocalDateTime time = LocalDateTime.now();
        StatusUpdate status = TestUtilities.createStatusUpdate("test", time);
        this.statusUpdateRepository.save(status);

        status = this.statusUpdateRepository.findAll().get(0);
        assertNotNull(status);
        assertEquals("test", status.getContent());
    }

    @Test
    @Transactional
    public void addMultipleSimpleStatusUpdate() {
        LocalDateTime time = LocalDateTime.now();
        StatusUpdate status = TestUtilities.createStatusUpdate("test", time);
        this.statusUpdateRepository.save(status);

        status = TestUtilities.createStatusUpdate("second", time);
        this.statusUpdateRepository.save(status);

        List<StatusUpdate> all = this.statusUpdateRepository.findAll();
        assertEquals(2, all.size());
    }
}
