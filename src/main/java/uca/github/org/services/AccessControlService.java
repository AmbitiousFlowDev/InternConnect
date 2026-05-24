package uca.github.org.services;

import uca.github.org.models.Permission;
import uca.github.org.models.User;

public interface AccessControlService {
    boolean hasRole(User user, User.Role role);

    boolean hasPermission(User user, Permission permission);

    boolean canPublishOffers(User user);

    boolean canManageAnyOffer(User user);

    boolean canManageOwnOffers(User user);

    boolean canViewOfferApplications(User user);

    boolean canManageRoles(User user);

    boolean canAssignRoles(User user);

    boolean canViewUsers(User user);
}
