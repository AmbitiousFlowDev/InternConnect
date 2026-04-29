package uca.github.org.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uca.github.org.models.User;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.AuthService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Handles the logic for creating a new user account.
     */
    @Override
    @Transactional
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(User.AccountStatus.ACTIVE);
        user.setRegistrationDate(LocalDate.now());
        return userRepository.save(user);
    }

    /**
     * Utility method to prevent duplicate accounts during registration.
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Core Spring Security method used to authenticate a user during login.
     * It maps our custom 'User' entity to the Security context.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}