package projekti.controllers;

import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import projekti.TestUtilities;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.enums.FriendshipState;
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendlingTest extends FluentTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FriendRepository friendRepository;

    @Before
    public void before() {
        Account account = TestUtilities.createFullAccount("john", passwordEncoder.encode("john"), "john", "eagle");
        accountRepository.save(account);

        account = TestUtilities.createFullAccount("miina", passwordEncoder.encode("miina"), "miina", "gronroos");
        accountRepository.save(account);

        account = TestUtilities.createFullAccount("meri", passwordEncoder.encode("meri"), "meri", "kuusela");
        accountRepository.save(account);
    }

    @After
    public void after() {
        this.friendRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    public void canSearchAccount() {
        loginWithCredentials("miina", "miina");

        find("#searchField").fill().with("john");
        find("#search-submit").submit();

        assertThat(pageSource()).contains("Search for Friends");
        assertThat(pageSource()).contains("john eagle");
        assertThat(pageSource()).doesNotContain("meri kuusela");
        assertThat(pageSource()).doesNotContain("miina gronroos");
    }

    @Test
    public void canSearchMultipleAccount() {
        loginWithCredentials("john", "john");

        find("#searchField").fill().with("M");
        find("#search-submit").submit();

        assertThat(pageSource()).contains("Search for Friends");
        assertThat(pageSource()).doesNotContain("john eagle");
        assertThat(pageSource()).contains("meri kuusela");
        assertThat(pageSource()).contains("miina gronroos");
    }

    @Test
    public void canSearchItself() {
        loginWithCredentials("meri", "meri");

        find("#searchField").fill().with("M");
        find("#search-submit").submit();

        assertThat(pageSource()).contains("Search for Friends");
        assertThat(pageSource()).doesNotContain("john eagle");
        assertThat(pageSource()).contains("meri kuusela");
        assertThat(pageSource()).contains("miina gronroos");
    }

    @Test
    public void noButtonWhileSearchItself() {
        loginWithCredentials("meri", "meri");

        find("#searchField").fill().with("Meri");
        find("#search-submit").submit();

        assertThat(pageSource()).contains("Search for Friends");
        assertThat(pageSource()).contains("meri kuusela");
        assertThat(pageSource()).doesNotContain("Ask");
        assertThat(pageSource()).doesNotContain("john eagle");
        assertThat(pageSource()).doesNotContain("miina gronroos");
    }

    @Test
    public void canSeeFriendsAsList() {
        Account user1 = this.accountRepository.findByUsername("john");
        Account user2 = this.accountRepository.findByUsername("meri");
        Account user3 = this.accountRepository.findByUsername("miina");

        createFriendship(user1, user3);
        createFriendship(user2, user3);

        loginWithCredentials("miina", "miina");

        assertThat(pageSource()).contains("Friends");
        goTo("http://localhost:" + port + "/old-face/miinagronroos/friends");

        assertThat(pageSource()).contains("meri kuusela");
        assertThat(pageSource()).contains("john eagle");
    }

    private void createFriendship(Account sender, Account receiver) {
        Friend friend = new Friend();
        friend.setTimestamp(LocalDateTime.now());
        friend.setFriendshipState(FriendshipState.ACCEPTED);
        friend.setSender(sender);
        friend.setReceiver(receiver);

        this.friendRepository.save(friend);
    }

    private void loginWithCredentials(String username, String password) {
        goTo("http://localhost:" + port + "/login");
        assertTrue(pageSource().contains("Login"));

        find("#username").fill().with(username);
        find("#password").fill().with(password);
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Home"));
    }
}