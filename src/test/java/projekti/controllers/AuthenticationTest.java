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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTest extends FluentTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void before() {
        Account account = TestUtilities.createAccount("admin", passwordEncoder.encode("admin"));
        account.setNickname("test");
        account.getAuthorities().add("USER");

        this.accountRepository.save(account);
    }

    @After
    public void after() {
        this.accountRepository.deleteAll();
        this.friendRepository.deleteAll();
    }

    @Test
    public void mainPageFound() {
        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("Main Page");
    }

    @Test
    public void noAccessToMainApplicationAsAnynomous() {
        goTo("http://localhost:" + port + "/home");
        assertThat(pageSource()).contains("Login");

        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("Main Page");

        goTo("http://localhost:" + port + "/login");
        assertThat(pageSource()).contains("Login");

        goToPage("/register", "Registration page");

        goTo("http://localhost:" + port + "/old-face/test");
        assertThat(pageSource()).contains("Login");
    }

    @Test
    public void canLoginWithRealAccount() {
        goTo("http://localhost:" + port + "/login");
        assertThat(pageSource()).contains("Login");

        find("#username").fill().with("admin");
        find("#password").fill().with("admin");
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Home"));
    }

    @Test
    public void cannotLoginWithFakeAccount() {
        goTo("http://localhost:" + port + "/login");
        assertThat(pageSource()).contains("Login");

        find("#username").fill().with("haa");
        find("#password").fill().with("ei oikea");
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Login"));
        assertTrue(pageSource().contains("Invalid username or password"));
    }

    @Test
    public void canLoginAndLogout() {
        goToPage("/login", "Login");

        find("#username").fill().with("admin");
        find("#password").fill().with("admin");
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Home"));

        goToPage("/logout", "Login");

        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("Main Page");

        goToPage("/old-face/test", "Login");
    }

    @Test
    public void registerWithTakenUsername() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("admin");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("test123Moi");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OP");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("Username has been already taken!"));
    }

    @Test
    public void registerWithInvalidPassword() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("user");
        find("#password").fill().with("aa");
        find("#passwordAgain").fill().with("aa");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OP");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("Passwords length must be between 8-20 characters!"));
    }

    @Test
    public void registerWithFailedPasswordConfirm() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("aa");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OP");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("The password fields must match!"));
    }

    @Test
    public void registerWithTakenNickname() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("test123Moi");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("test");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("Nickname has been already taken!"));
    }

    @Test
    public void registerSuccess() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("test123Moi");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OP");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Login"));
    }

    @Test
    public void registerSuccessAndLoginSuccessfully() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("test123Moi");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OPMAN");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Login"));

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#login-submit").submit();

        assertTrue(pageSource().contains("Home"));
    }

    @Test
    public void registerWithMultipleErrors() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("admin");
        find("#password").fill().with("aa");
        find("#passwordAgain").fill().with("ff");
        find("#nickname").fill().with("test");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("Username has been already taken!"));
        assertTrue(pageSource().contains("Passwords length must be between 8-20 characters!"));
        assertTrue(pageSource().contains("The password fields must match!"));
        assertTrue(pageSource().contains("must not be empty"));
        assertTrue(pageSource().contains("Nickname has been already taken!"));
    }

    @Test
    public void registerSuccessAfterFixingErrors() {
        goToPage("/register", "Registration page");

        find("#username").fill().with("admin");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("ff");
        find("#firstName").fill().with("bill");
        find("#lastName").fill().with("man");
        find("#nickname").fill().with("OP");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Register"));
        assertTrue(pageSource().contains("Username has been already taken!"));
        assertTrue(pageSource().contains("The password fields must match!"));

        find("#username").fill().with("user");
        find("#password").fill().with("test123Moi");
        find("#passwordAgain").fill().with("test123Moi");
        find("#register-submit").submit();

        assertTrue(pageSource().contains("Login"));
    }

    private void goToPage(String s, String s2) {
        goTo("http://localhost:" + port + s);
        assertTrue(pageSource().contains(s2));
    }
}