package projekti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projekti.domain.entities.Account;
import projekti.repository.AccountRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Customized version of service that controls user details while logging in, checking authentication status etc.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Exception is handled by spring security so do not remove because then it will break most likely!
     * Loads account from database with username and returns it as spring securities own user object.
     *
     * @param username username of an account spring security wants to do something.
     *
     * @return found user to the spring security in the right format.
     *
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        List<SimpleGrantedAuthority> authorities = account.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}
