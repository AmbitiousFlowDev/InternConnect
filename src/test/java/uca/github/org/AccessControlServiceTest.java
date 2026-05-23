package uca.github.org;

import org.junit.jupiter.api.Test;
import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.models.User;
import uca.github.org.services.AccessControlService;
import uca.github.org.services.AccessControlServiceImpl;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccessControlServiceTest {

    private final AccessControlService accessControlService = new AccessControlServiceImpl();

    @Test
    void canPublishOffers_ShouldAllowManagedPermission() {
        User user = User.builder()
                .role(User.Role.USER)
                .assignedRoles(new LinkedHashSet<>(Set.of(Role.builder()
                        .name("RECRUITER")
                        .permissions(new LinkedHashSet<>(Set.of(Permission.PUBLISH_OFFERS)))
                        .build())))
                .build();

        assertThat(accessControlService.canPublishOffers(user)).isTrue();
    }

    @Test
    void canManageAnyOffer_ShouldAllowAdminFallbackRole() {
        User admin = User.builder()
                .role(User.Role.ADMIN)
                .build();

        assertThat(accessControlService.canManageAnyOffer(admin)).isTrue();
    }

    @Test
    void canManageOwnOffers_ShouldRejectPlainUser() {
        User user = User.builder()
                .role(User.Role.USER)
                .build();

        assertThat(accessControlService.canManageOwnOffers(user)).isFalse();
    }
}
