package projekti.utils.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import projekti.models.Account;
import projekti.models.Friend;
import projekti.repository.AccountRepository;
import projekti.services.AccountService;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object value) {
        if ((auth == null) || (targetDomainObject == null) || !(value instanceof String)){
            return false;
        }

        return hasAccess(auth, value.toString());
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object value) {
        if ((auth == null) || (targetType == null) || !(value instanceof String)) {
            return false;
        }

        return hasAccess(auth, value.toString());
    }

    private boolean hasAccess(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        if (user.getNickname().equals(value)) {
            return true;
        }

        for (Friend friend : user.getReceiverFriends()) {
            if (friend.getStatus() > 0 && friend.getSender().getNickname().equals(value)) {
                return true;
            }
        }

        for (Friend friend : user.getSentFriends()) {
            if (friend.getStatus() > 0 && friend.getReceiver().getNickname().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
