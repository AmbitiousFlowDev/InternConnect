package uca.github.org;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.models.User;
import uca.github.org.repositories.RoleRepository;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.RoleManagementService;
import uca.github.org.services.RoleManagementServiceImpl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleManagementServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    private RoleManagementService roleManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleManagementService = new RoleManagementServiceImpl(roleRepository, userRepository);
    }

    @Test
    void assignRole_ShouldUpdateLegacyRoleAndManagedRole() {
        User user = User.builder()
                .id(1L)
                .role(User.Role.USER)
                .build();
        Role posterRole = Role.builder()
                .id(2L)
                .name("POSTER")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(posterRole));
        when(userRepository.save(user)).thenReturn(user);

        User result = roleManagementService.assignRole(1L, 2L);

        assertThat(result.getRole()).isEqualTo(User.Role.POSTER);
        assertThat(result.getAssignedRoles()).containsExactly(posterRole);
        verify(userRepository).save(user);
    }

    @Test
    void updatePermissions_ShouldReplaceRolePermissions() {
        Role role = Role.builder()
                .id(2L)
                .name("POSTER")
                .permissions(new LinkedHashSet<>(Set.of(Permission.PUBLISH_OFFERS)))
                .build();
        Set<Permission> updatedPermissions = Set.of(
                Permission.PUBLISH_OFFERS,
                Permission.VIEW_OFFER_APPLICATIONS
        );

        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleManagementService.updatePermissions(2L, updatedPermissions);

        assertThat(result.getPermissions()).containsExactlyInAnyOrderElementsOf(updatedPermissions);
        verify(roleRepository).save(role);
    }
}
