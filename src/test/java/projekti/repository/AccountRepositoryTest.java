package projekti.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import projekti.TestUtilities;
import projekti.domain.entities.Account;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @After
    public void after() {
        this.accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void addSimpleAccount() {
        Account account = TestUtilities.createAccount("John", "Eagle");

        this.accountRepository.save(account);
        account = this.accountRepository.findByUsername("John");

        assertNotNull(account);
        assertEquals("John", account.getUsername());
        assertEquals("Eagle", account.getPassword());
    }

    @Test
    @Transactional
    public void addMultipleSimpleAccounts() {
        Account account = TestUtilities.createAccount("admin", "12345");
        Account another = TestUtilities.createAccount("Unknown", "Clasified");

        this.accountRepository.save(account);
        this.accountRepository.save(another);

        account = this.accountRepository.findByUsername("admin");
        another = this.accountRepository.findByUsername("Unknown");

        assertNotNull(account);
        assertNotNull(another);
        assertEquals("admin", account.getUsername());
        assertEquals("Clasified", another.getPassword());
    }

    @Test
    @Transactional
    public void authoritiesCanBeAdded() {
        Account account = TestUtilities.createAccount("user", "notAdmin");
        account.getAuthorities().add("USER");

        this.accountRepository.save(account);
        account = this.accountRepository.findByUsername("user");

        assertNotNull(account);
        assertEquals(1, account.getAuthorities().size());
        assertEquals("USER", account.getAuthorities().get(0));
    }

    @Test
    @Transactional
    public void multipleAuthoritiesCanBeAdded() {
        Account account = TestUtilities.createAccount("user", "notAdmin");
        List<String> auth = account.getAuthorities();

        auth.add("USER");
        auth.add("TESTER");
        auth.add("ADMIN");
        auth.add("POSTER");

        account.setAuthorities(auth);
        this.accountRepository.save(account);
        account = this.accountRepository.findByUsername("user");

        assertNotNull(account);
        assertEquals(4, account.getAuthorities().size());
        assertTrue(account.getAuthorities().contains("USER"));
        assertTrue(account.getAuthorities().contains("TESTER"));
        assertTrue(account.getAuthorities().contains("POSTER"));
        assertTrue(account.getAuthorities().contains("ADMIN"));
    }

    @Test
    @Transactional
    public void allFieldsNotRelating() {
        Account account = TestUtilities.createAccount("xd", "xd");
        account.setAddress("Gustaf Hällströmin katu 2B");
        account.setAddressNumber("00560");
        account.setCity("Helsinki");
        account.setBorn(LocalDate.of(1998, 5, 28));
        account.setEmail("askepore@gmail.com");
        account.setFirstName("Admin");
        account.setLastName("Badass");
        account.setPhoneNumber("0501234678");
        account.setNickname("Makke");

        this.accountRepository.save(account);
        account = this.accountRepository.findByUsername("xd");

        assertNotNull(account);
        assertEquals("Helsinki", account.getCity());
        assertEquals("Admin", account.getFirstName());
        assertEquals("Gustaf Hällströmin katu 2B", account.getAddress());
        assertEquals("0501234678", account.getPhoneNumber());
        assertEquals(0, account.getBorn().compareTo(LocalDate.of(1998, 5, 28)));
    }

    @Test
    @Transactional
    public void everythingComesInFindAll() {
        this.accountRepository.save(TestUtilities.createAccount("user", "notAdmin"));
        this.accountRepository.save(TestUtilities.createAccount("xd", "xd"));
        this.accountRepository.save(TestUtilities.createAccount("Unknown", "Clasified"));
        this.accountRepository.save(TestUtilities.createAccount("John", "Eagle"));
        this.accountRepository.save(TestUtilities.createAccount("admin", "12345"));

        List<Account> people = this.accountRepository.findAll();

        assertEquals(5, people.size());
        assertEquals("Unknown", people.get(2).getUsername());
        assertEquals("notAdmin", people.get(0).getPassword());
        assertEquals("John", people.get(3).getUsername());
        assertEquals("12345", people.get(4).getPassword());
    }
}
