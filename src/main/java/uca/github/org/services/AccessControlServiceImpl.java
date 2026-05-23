package uca.github.org.services;

import org.springframework.stereotype.Service;
import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.models.User;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    @Override
    public boolean hasRole(User user, User.Role role) {
        if (user == null || role == null) {
            return false;
        }

        return user.getRole() == role
                || user.getAssignedRoles().stream()
                .map(Role::getName)
                .anyMatch(role.name()::equals);
    }

    @Override
    public boolean hasPermission(User user, Permission permission) {
        if (user == null || permission == null) {
            return false;
        }

        return user.getAssignedRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission::equals);
    }

    @Override
    public boolean canPublishOffers(User user) {
        return hasPermission(user, Permission.PUBLISH_OFFERS)
                || hasRole(user, User.Role.POSTER)
                || hasRole(user, User.Role.ADMIN);
    }

    @Override
    public boolean canManageAnyOffer(User user) {
        return hasPermission(user, Permission.MANAGE_ANY_OFFER)
                || hasRole(user, User.Role.ADMIN);
    }

    @Override
    public boolean canManageOwnOffers(User user) {
        return hasPermission(user, Permission.MANAGE_OWN_OFFERS)
                || hasRole(user, User.Role.POSTER)
                || hasRole(user, User.Role.ADMIN);
    }

    @Override
    public boolean canViewOfferApplications(User user) {
        return hasPermission(user, Permission.VIEW_OFFER_APPLICATIONS)
                || hasRole(user, User.Role.POSTER)
                || hasRole(user, User.Role.ADMIN);
    }
}
