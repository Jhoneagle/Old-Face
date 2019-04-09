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
import projekti.models.Reaction;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        LocalDate time = LocalDate.now();
        Reaction reaction = TestUtilities.createReaction("amazing", 1L, time);
        this.reactionRepository.save(reaction);

        reaction = this.reactionRepository.findByTimestamp(time);
        assertNotNull(reaction);
        assertEquals("amazing", reaction.getContent());
        assertEquals(1, (long) reaction.getStatus());
    }

    @Test
    @Transactional
    public void addMultipleSimpleStatusUpdate() {
        LocalDate time = LocalDate.now();
        Reaction reaction = TestUtilities.createReaction("amazing", 1L, time);
        this.reactionRepository.save(reaction);

        reaction = TestUtilities.createReaction("amazing", 1L, time);
        this.reactionRepository.save(reaction);

        List<Reaction> all = this.reactionRepository.findAll();
        assertEquals(2, all.size());
    }
}
