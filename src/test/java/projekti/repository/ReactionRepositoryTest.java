package projekti.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import projekti.TestUtilities;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReactionRepositoryTest {
    @Autowired
    private ReactionRepository reactionRepository;

    @Before
    public void before() {
        this.reactionRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleReaction() {

    }
}
