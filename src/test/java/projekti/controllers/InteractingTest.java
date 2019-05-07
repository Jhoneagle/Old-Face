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
import projekti.repository.AccountRepository;
import projekti.repository.FriendRepository;
import projekti.repository.StatusUpdateRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InteractingTest extends FluentTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StatusUpdateRepository statusUpdateRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        this.statusUpdateRepository.deleteAll();
        this.friendRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    public void canFindPostPanel() {
        loginWithCredentials("miina", "miina");

        assertThat(pageSource()).contains("Create status update...");
        assertThat(pageSource()).contains("Post");
        assertThat(pageSource()).contains("Friends");
        assertThat(pageSource()).contains("miina gronroos");
    }

    @Test
    public void cannotSeePostPanelOfStranger() {
        loginWithCredentials("miina", "miina");
        goTo("http://localhost:" + port + "/old-face/johneagle");

        assertThat(pageSource()).doesNotContain("Create friendshipState update...");
        assertThat(pageSource()).doesNotContain("Post");
        assertThat(pageSource()).contains("Wall");
        assertThat(pageSource()).contains("Album");
        assertThat(pageSource()).contains("john eagle");
    }

    @Test
    public void canMakeStatusUpdate() {
        loginWithCredentials("john", "john");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String time = LocalDateTime.now().format(formatter);

        assertFalse(pageSource().contains(time));
        assertFalse(pageSource().contains("this is test post"));

        find("#content").fill().with("this is test post");
        find("#post-submit").submit();

        assertTrue(pageSource().contains(time));
        assertTrue(pageSource().contains("this is test post"));
    }

    @Test
    public void maxTenStatusUpdatesShown() {
        loginWithCredentials("john", "john");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String time = LocalDateTime.now().format(formatter);

        for (int i = 1; i <= 30; i++) {
            find("#content").fill().with("this is test post number: " + i + ".");
            find("#post-submit").submit();
        }

        assertTrue(pageSource().contains(time));
        assertTrue(pageSource().contains("this is test post number: 30"));
        assertTrue(pageSource().contains("this is test post number: 22"));
        assertTrue(pageSource().contains("this is test post number: 6"));

        assertFalse(pageSource().contains("this is test post number: 1."));
        assertFalse(pageSource().contains("this is test post number: 5"));
        assertFalse(pageSource().contains("this is test post number: 31"));
    }

    @Test
    public void canSeeAndAddComment() {
        loginWithCredentials("john", "john");

        find("#content").fill().with("this is test post");
        find("#post-submit").submit();

        assertFalse(el("#comment").displayed());

        assertTrue(pageSource().contains("Show comments"));
        $("button", withText("Show comments")).click();

        assertTrue(el("#comment").displayed());
    }

    @Test
    public void canSeeButNotAddCommentToStranger() {
        loginWithCredentials("miina", "miina");

        find("#content").fill().with("this is test post");
        find("#post-submit").submit();

        assertTrue(pageSource().contains("Leave a Comment:"));

        logOut();
        loginWithCredentials("meri", "meri");
        goTo("http://localhost:" + port + "/old-face/miinagronroos");

        assertFalse(pageSource().contains("Leave a Comment:"));
        assertFalse(el("#showComments").displayed());

        assertTrue(pageSource().contains("Show comments"));
        $("button", withText("Show comments")).click();

        assertFalse(pageSource().contains("Leave a Comment:"));
        assertTrue(el("#showComments").displayed());
    }

    private void loginWithCredentials(String username, String password) {
        goTo("http://localhost:" + port + "/login");
        assertTrue(pageSource().contains("Login"));

        find("#username").fill().with(username);
        find("#password").fill().with(password);
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Home"));
    }

    private void logOut() {
        goTo("http://localhost:" + port + "/logout");
        assertTrue(pageSource().contains("Login"));
    }
}