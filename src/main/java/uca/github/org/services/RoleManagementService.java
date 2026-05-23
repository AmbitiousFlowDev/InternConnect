package uca.github.org.services;

import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.models.User;

import java.util.List;
import java.util.Set;

public interface RoleManagementService {
    List<Role> getAllRoles();

    List<User> getAllUsers();

    User assignRole(Long userId, Long roleId);

    Role updatePermissions(Long roleId, Set<Permission> permissions);
}
