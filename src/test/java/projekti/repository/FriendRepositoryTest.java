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
        LocalDate time = LocalDate.now();
        Friend first = TestUtilities.createFriend(12, time);

        this.friendRepository.save(first);
        first = this.friendRepository.findAll().get(0);

        assertNotNull(first);
        assertEquals(12, (long) first.getStatus());
        assertTrue(first.getTimestamp().compareTo(time) == 0);
    }

    @Test
    @Transactional
    public void addMultipleSimpleUnrelatedFriend() {
        LocalDate time = LocalDate.now();
        Friend first = TestUtilities.createFriend(100, time);
        Friend second = TestUtilities.createFriend(0, time);
        Friend third = TestUtilities.createFriend(666, time);

        this.friendRepository.save(first);
        this.friendRepository.save(second);
        this.friendRepository.save(third);
        List<Friend> friends = this.friendRepository.findAll();

        assertEquals(3, friends.size());
        assertTrue(friends.get(1).getTimestamp().compareTo(second.getTimestamp()) == 0);
        assertEquals(first.getStatus(), friends.get(0).getStatus());
        assertEquals(third.getStatus(), friends.get(2).getStatus());
    }

    // This test doesnt fucking work!!!! omg!!!
    @Test
    @Transactional
    public void addSimpleRelatedFriend() {
        LocalDate time = LocalDate.now();
        Friend first = TestUtilities.createFriend(0, time);
        this.friendRepository.save(first);
        first = this.friendRepository.findByTimestamp(time);

        Account sender = TestUtilities.createAccount("admin", "admin");
        Account receiver = TestUtilities.createAccount("owner", "owner");
        this.accountRepository.save(sender);
        this.accountRepository.save(receiver);

        List<Account> users = this.accountRepository.findAll();
        sender = users.get(0);
        receiver = users.get(1);

        first.setSender(sender);
        first.setReceiver(receiver);

        this.friendRepository.save(first);
        first = this.friendRepository.findAll().get(0);

        assertNotNull(first);
        assertNotNull(first.getReceiver());
        assertEquals(sender.getUsername(), first.getSender().getUsername());
        assertEquals(sender.getId(), first.getSender().getId());

        Account isItReal = this.accountRepository.getOne(first.getReceiver().getId());
        assertNotNull(isItReal);
        assertEquals(receiver.getPassword(), isItReal.getPassword());
        assertEquals(receiver.getId(), isItReal.getId());
        assertEquals(1, isItReal.getReceiverFriends().size());
        
        isItReal = this.accountRepository.getOne(sender.getId());
        assertNotNull(isItReal);
        assertEquals(1, isItReal.getSentFriends().size());
    }
}
