package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("java:S1948")
public class User implements UserDetails {

    public enum Role {
        VISITOR, USER, POSTER, ADMIN
    }

    public enum AccountStatus {
        ACTIVE, SUSPENDED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<uca.github.org.models.Role> assignedRoles = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "poster", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Internship> postedInternships = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Message> sentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Message> receivedMessages = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<SimpleGrantedAuthority> primaryRole = role == null
                ? Stream.empty()
                : Stream.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        Stream<SimpleGrantedAuthority> managedRoleAuthorities = managedRoles().stream()
                .filter(Objects::nonNull)
                .map(managedRole -> new SimpleGrantedAuthority(managedRole.getAuthorityName()));

        Stream<SimpleGrantedAuthority> permissionAuthorities = managedRoles().stream()
                .filter(Objects::nonNull)
                .flatMap(managedRole -> managedRole.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.name()));

        return Stream.of(primaryRole, managedRoleAuthorities, permissionAuthorities)
                .flatMap(authorities -> authorities)
                .distinct()
                .toList();
    }

    public boolean hasAssignedRole(Long roleId) {
        return roleId != null && managedRoles().stream()
                .filter(Objects::nonNull)
                .map(uca.github.org.models.Role::getId)
                .anyMatch(roleId::equals);
    }

    private Set<uca.github.org.models.Role> managedRoles() {
        if (assignedRoles == null) {
            assignedRoles = new LinkedHashSet<>();
        }
        return assignedRoles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }
}
