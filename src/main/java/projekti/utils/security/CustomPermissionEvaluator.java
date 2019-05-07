package projekti.utils.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import projekti.domain.entities.Account;
import projekti.domain.entities.Friend;
import projekti.domain.enums.FriendshipState;
import projekti.repository.AccountRepository;

import java.io.Serializable;

/**
 * Custom permission evaluator to replace spring securities default one. Main point is that this implementation is much more flexible then spring default.
 * Allows to preauthorize if user is doing something to something that he technically owns or does he has access as a friend or maybe want to have
 * some extra restrictions to make sure any rules ain't broken.
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method is mainly called if user types to sec:authority or to preauthorize annotation following syntax: "hasPermission('string', 'string')".
     *
     * @param auth spring securities authentication context holder object.
     * @param targetDomainObject first parameter in the syntax.
     * @param value second parameter in the syntax.
     *
     * @return return <code>true</code> if the user has permission to proceed and otherwise <code>false</code>.
     */
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object value) {
        if ((auth == null) || !(value instanceof String) || !(targetDomainObject instanceof String)){
            return false;
        }

        // Checking which level of authorization is needed to do.
        if (((String) targetDomainObject).equalsIgnoreCase("access")) {
            return isFriendOrOwner(auth, value.toString());
        } else if (((String) targetDomainObject).equalsIgnoreCase("owner")) {
            return isOwner(auth, value.toString());
        } else if (((String) targetDomainObject).equalsIgnoreCase("albumOwner")) {
            return isOwnerAndImagesNotTooMany(auth, value.toString());
        }

        return false;
    }

    /**
     * This is another way of calling 'haspermission' in sec:authority or to preauthorize annotation.
     *
     * @param auth spring securities authentication context holder object.
     * @param targetId not sure exactly.
     * @param targetType not sure exactly.
     * @param value not sure exactly.
     *
     * @return return <code>true</code> if the user has permission to proceed and otherwise <code>false</code>.
     */
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object value) {
        if ((auth == null) || (targetType == null) || !(value instanceof String)) {
            return false;
        }

        return isFriendOrOwner(auth, value.toString());
    }

    /**
     * Is user doing something to his own stuff or friends stuff?
     *
     * @param auth spring securities authentication context holder object.
     * @param value nickname of the user whose content is accessed.
     *
     * @return return <code>true</code> if the user has permission to proceed and otherwise <code>false</code>.
     */
    private boolean isFriendOrOwner(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        if (user.getNickname().equals(value)) {
            return true;
        }

        for (Friend friend : user.getReceiverFriends()) {
            if (friend.getFriendshipState() == FriendshipState.ACCEPTED && friend.getSender().getNickname().equals(value)) {
                return true;
            }
        }

        for (Friend friend : user.getSentFriends()) {
            if (friend.getFriendshipState() == FriendshipState.ACCEPTED && friend.getReceiver().getNickname().equals(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is user editing his own stuff?
     *
     * @param auth spring securities authentication context holder object.
     * @param value nickname of the user whose content is accessed.
     *
     * @return return <code>true</code> if the user has permission to proceed and otherwise <code>false</code>.
     */
    private boolean isOwner(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        return user.getNickname().equals(value);
    }

    /**
     * Is user editing his own stuff and not adding too many pictures?
     *
     * @param auth spring securities authentication context holder object.
     * @param value nickname of the user whose content is accessed.
     *
     * @return return <code>true</code> if the user has permission to proceed and otherwise <code>false</code>.
     */
    private boolean isOwnerAndImagesNotTooMany(Authentication auth, String value) {
        Account user = accountRepository.findByUsername(auth.getName());
        return user.getNickname().equals(value) && user.getImages().size() < 10;

    }
}
