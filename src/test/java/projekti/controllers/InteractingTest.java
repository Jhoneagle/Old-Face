package projekti.controllers;

import org.fluentlenium.adapter.junit.FluentTest;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private PasswordEncoder passwordEncoder;

    @Before
    public void before() {
        this.accountRepository.deleteAll();

        Account account = TestUtilities.createFullAccount("john", passwordEncoder.encode("john"), "john", "eagle");
        accountRepository.save(account);

        account = TestUtilities.createFullAccount("miina", passwordEncoder.encode("miina"), "miina", "gronroos");
        accountRepository.save(account);

        account = TestUtilities.createFullAccount("meri", passwordEncoder.encode("meri"), "meri", "kuusela");
        accountRepository.save(account);
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

        assertThat(pageSource()).doesNotContain("Create status update...");
        assertThat(pageSource()).doesNotContain("Post");
        assertThat(pageSource()).contains("Wall");
        assertThat(pageSource()).contains("Album");
        assertThat(pageSource()).contains("john eagle");
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