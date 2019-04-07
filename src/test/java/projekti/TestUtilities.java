package projekti;

import projekti.models.Account;
import projekti.models.Friend;

import java.time.LocalDate;

public class TestUtilities {
    public static Friend createFriend(long status, LocalDate time) {
        Friend first = new Friend();
        first.setStatus(status);
        first.setTimestamp(time);
        return first;
    }

    public static Account createAccount(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        return account;
    }
}
