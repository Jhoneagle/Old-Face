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
import projekti.domain.entities.Friend;
import projekti.domain.enums.FriendshipState;

import java.time.LocalDateTime;
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

    @After
    public void after() {
        this.friendRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleUnrelatedFriend() {
        LocalDateTime time = LocalDateTime.now();
        Friend first = TestUtilities.createFriend(FriendshipState.ACCEPTED, time);

        this.friendRepository.save(first);
        first = this.friendRepository.findAll().get(0);

        assertNotNull(first);
        assertEquals(FriendshipState.ACCEPTED, first.getFriendshipState());
        assertEquals(0, first.getTimestamp().compareTo(time));
    }

    @Test
    @Transactional
    public void addMultipleSimpleUnrelatedFriend() {
        LocalDateTime time = LocalDateTime.now();
        Friend first = TestUtilities.createFriend(FriendshipState.ACCEPTED, time);
        Friend second = TestUtilities.createFriend(FriendshipState.NOT_REQUESTED, time);
        Friend third = TestUtilities.createFriend(FriendshipState.ACCEPTED, time);

        this.friendRepository.save(first);
        this.friendRepository.save(second);
        this.friendRepository.save(third);
        List<Friend> friends = this.friendRepository.findAll();

        assertEquals(3, friends.size());
        assertEquals(0, friends.get(1).getTimestamp().compareTo(second.getTimestamp()));
        assertEquals(first.getFriendshipState(), friends.get(0).getFriendshipState());
        assertEquals(third.getFriendshipState(), friends.get(2).getFriendshipState());
    }
}
