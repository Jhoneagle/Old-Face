package projekti.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import javax.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import projekti.models.Friend;
import projekti.models.Account;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FriendRepositoryTest {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Before
    public void before() {
        this.friendRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleUnrelatedFriend() {
        Friend first = new Friend();
        LocalDate time = LocalDate.now();
        first.setStatus(12L);
        first.setTimestamp(time);

        this.friendRepository.save(first);
        first = this.friendRepository.findAll().get(0);

        assertNotNull(first);
        assertEquals(12L, (long) first.getStatus());
        assertTrue(first.getTimestamp().compareTo(time) == 0);
    }


}
