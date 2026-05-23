package uca.github.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import uca.github.org.models.User;
import uca.github.org.repositories.RoleRepository;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.AuthServiceImpl;

import java.util.Optional;

/**
 * Unit and Integration tests for {@link AuthServiceImpl}.
 * This class verifies the business logic for user registration and
 * account management, ensuring that dependencies like the repository
 * and password encoder are called correctly.
 * * @author InternConnect Team
 * 
 * @version 1.0
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    /**
     * Set up a fresh User object before each test execution.
     * This ensures test isolation and a consistent baseline.
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@uca.ma")
                .password("rawPassword")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();
    }

    /**
     * Tests the registration process.
     * <p>
     * Verifies that:
     * 1. The password is encrypted using the PasswordEncoder.
     * 2. The user status is set to ACTIVE by default.
     * 3. A registration date is assigned.
     * 4. The data is persisted via the UserRepository.
     * </p>
     */
    @Test
    @DisplayName("Should successfully register a new user with encoded password")
    void register_ShouldSaveUserWithEncodedPassword() {
        String encodedPass = "encodedHash123";
        when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPass);
        when(roleRepository.findByName(User.Role.USER.name())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        User savedUser = authService.register(testUser);
        assertNotNull(savedUser, "The saved user should not be null");
        assertEquals(encodedPass, savedUser.getPassword(), "The password should be the encoded hash");
        assertEquals(User.AccountStatus.ACTIVE, savedUser.getStatus(), "New users should be ACTIVE by default");
        assertNotNull(savedUser.getRegistrationDate(), "Registration date should be automatically set");
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(testUser);
    }

    /**
     * Tests the email availability check.
     * <p>
     * Ensures that the service correctly proxies the request to the
     * repository and returns the expected boolean value.
     * </p>
     */
    @Test
    @DisplayName("Should return true when email already exists")
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        // Given
        String email = "test@uca.ma";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = authService.existsByEmail(email);

        // Then
        assertTrue(exists, "Service should return true if the repository finds the email");
        verify(userRepository, times(1)).existsByEmail(email);
    }
}
