package projekti.utils.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.repository.AccountRepository;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object value) {
        if ((auth == null) || !(value instanceof String) || !(targetDomainObject instanceof String)){
            return false;
        }

        if (((String) targetDomainObject).equalsIgnoreCase("access")) {
            return isFriendOrOwner(auth, value.toString());
        } else if (((String) targetDomainObject).equalsIgnoreCase("owner")) {
            return isOwner(auth, value.toString());
        } else if (((String) targetDomainObject).equalsIgnoreCase("albumOwner")) {
            return isOwnerAndImagesNotTooMany(auth, value.toString());
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object value) {
        if ((auth == null) || (targetType == null) || !(value instanceof String)) {
            return false;
        }

        return isFriendOrOwner(auth, value.toString());
    }

    private boolean isFriendOrOwner(Authentication auth, String value) {
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

    private boolean isOwner(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        return user.getNickname().equals(value);
    }

    private boolean isOwnerAndImagesNotTooMany(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        return user.getNickname().equals(value) && user.getImages().size() < 10;

    }
}
