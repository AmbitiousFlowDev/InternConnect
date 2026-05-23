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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable."));

        user.setRole(toLegacyRole(role));
        user.getAssignedRoles().clear();
        user.getAssignedRoles().add(role);

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

    private User.Role toLegacyRole(Role role) {
        try {
            return User.Role.valueOf(role.getName());
        } catch (IllegalArgumentException ex) {
            return User.Role.USER;
        }
    }
}
