package uca.github.org.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uca.github.org.models.Permission;
import uca.github.org.models.RecruiterVerification;
import uca.github.org.models.Role;
import uca.github.org.models.User;
import uca.github.org.repositories.RecruiterVerificationRepository;

import java.util.Set;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    @Autowired(required = false)
    private RecruiterVerificationRepository recruiterVerificationRepository;

    @Override
    public boolean hasRole(User user, User.Role role) {
        if (user == null || role == null) {
            return false;
        }

        return user.getRole() == role
                || assignedRoles(user).stream()
                .map(Role::getName)
                .anyMatch(role.name()::equals);
    }

    @Override
    public boolean hasPermission(User user, Permission permission) {
        if (user == null || permission == null) {
            return false;
        }

        return assignedRoles(user).stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission::equals);
    }

    @Override
    public boolean canPublishOffers(User user) {
        if (hasRole(user, User.Role.ADMIN)) {
            return true;
        }
        return hasRole(user, User.Role.POSTER) && isRecruiterApproved(user);
    }

    @Override
    public boolean canApplyToOffers(User user) {
        return hasRole(user, User.Role.USER);
    }

    @Override
    public boolean canSaveOffers(User user) {
        return hasRole(user, User.Role.USER);
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

    @Override
    public boolean canManageRoles(User user) {
        return hasPermission(user, Permission.MANAGE_ROLES)
                || hasRole(user, User.Role.ADMIN);
    }

    @Override
    public boolean canAssignRoles(User user) {
        return hasPermission(user, Permission.ASSIGN_ROLES)
                || hasRole(user, User.Role.ADMIN);
    }

    @Override
    public boolean canViewUsers(User user) {
        return hasPermission(user, Permission.VIEW_USERS)
                || hasRole(user, User.Role.ADMIN);
    }

    private Set<Role> assignedRoles(User user) {
        return user.getAssignedRoles() == null ? Set.of() : user.getAssignedRoles();
    }

    private boolean isRecruiterApproved(User user) {
        if (user == null || recruiterVerificationRepository == null) {
            return false;
        }
        return recruiterVerificationRepository.existsByRecruiterAndStatus(
                user,
                RecruiterVerification.VerificationStatus.APPROVED
        );
    }
}
