package uca.github.org.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uca.github.org.models.Permission;
import uca.github.org.models.Role;
import uca.github.org.repositories.RoleRepository;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        ensureRole("USER", "Etudiant", "Consulte et postule aux offres.", Set.of(Permission.APPLY_TO_OFFERS));
        ensureRole("POSTER", "Recruteur", "Publie et gere ses offres.", Set.of(
                Permission.PUBLISH_OFFERS,
                Permission.MANAGE_OWN_OFFERS,
                Permission.VIEW_OFFER_APPLICATIONS
        ));
        ensureRole("ADMIN", "Administrateur", "Gere les roles, les utilisateurs et toutes les offres.", Set.of(
                Permission.MANAGE_ROLES,
                Permission.ASSIGN_ROLES,
                Permission.VIEW_USERS,
                Permission.PUBLISH_OFFERS,
                Permission.MANAGE_OWN_OFFERS,
                Permission.MANAGE_ANY_OFFER,
                Permission.VIEW_OFFER_APPLICATIONS,
                Permission.APPLY_TO_OFFERS
        ));
    }

    private void ensureRole(String name, String label, String description, Set<Permission> permissions) {
        if (roleRepository.existsByName(name)) {
            return;
        }

        roleRepository.save(Role.builder()
                .name(name)
                .label(label)
                .description(description)
                .permissions(new LinkedHashSet<>(permissions))
                .build());
    }
}
