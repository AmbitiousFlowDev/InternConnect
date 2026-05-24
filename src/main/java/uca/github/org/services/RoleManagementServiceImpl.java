package uca.github.org.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.models.User;
import uca.github.org.repositories.RoleRepository;
import uca.github.org.repositories.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Override
    @Transactional
    public User assignRole(Long userId, Long roleId) {
        return assignRoles(userId, Set.of(roleId));
    }

    @Override
    @Transactional
    public User assignRoles(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        Set<Long> requestedRoleIds = roleIds == null ? Set.of() : roleIds;
        List<Role> roles = roleRepository.findAllById(requestedRoleIds);

        if (roles.size() != requestedRoleIds.size()) {
            throw new EntityNotFoundException("Un ou plusieurs roles sont introuvables.");
        }

        user.setRole(resolvePrimaryRole(roles));
        user.getAssignedRoles().clear();
        user.getAssignedRoles().addAll(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Role updatePermissions(Long roleId, Set<Permission> permissions) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable."));

        role.setPermissions(new LinkedHashSet<>(permissions));
        return roleRepository.save(role);
    }

    private User.Role resolvePrimaryRole(List<Role> roles) {
        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roleNames.contains(User.Role.ADMIN.name())) {
            return User.Role.ADMIN;
        }
        if (roleNames.contains(User.Role.POSTER.name())) {
            return User.Role.POSTER;
        }
        if (roleNames.contains(User.Role.USER.name())) {
            return User.Role.USER;
        }
        if (roleNames.contains(User.Role.VISITOR.name())) {
            return User.Role.VISITOR;
        }
        return User.Role.USER;
    }
}
