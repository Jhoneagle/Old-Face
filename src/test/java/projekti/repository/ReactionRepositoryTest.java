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
import projekti.domain.entities.Reaction;
import projekti.domain.enums.ReactionType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReactionRepositoryTest {
    @Autowired
    private ReactionRepository reactionRepository;

    @After
    public void after() {
        this.reactionRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleReaction() {
        LocalDateTime time = LocalDateTime.now();
        Reaction reaction = TestUtilities.createReaction("amazing", ReactionType.COMMENT, time);
        this.reactionRepository.save(reaction);

        reaction = this.reactionRepository.findAll().get(0);
        assertNotNull(reaction);
        assertEquals("amazing", reaction.getContent());
        assertEquals(ReactionType.COMMENT, reaction.getReactionType());
    }

    @Test
    @Transactional
    public void addMultipleSimpleStatusUpdate() {
        LocalDateTime time = LocalDateTime.now();
        Reaction reaction = TestUtilities.createReaction("amazing", ReactionType.COMMENT, time);
        this.reactionRepository.save(reaction);

        reaction = TestUtilities.createReaction("amazing", ReactionType.COMMENT, time);
        this.reactionRepository.save(reaction);

        List<Reaction> all = this.reactionRepository.findAll();
        assertEquals(2, all.size());
    }
}
