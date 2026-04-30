package uca.github.org.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for security settings in the InternConnect application.
 * This class can be used to define security-related beans, such as
 * authentication providers,
 * password encoders, and security filters.
 * 
 * Note : to be finished immidiately after the implementation of the
 * authentication and authorization features.
 */
@Configuration
@EnableWebSecurity
public class InternConnectSecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/home", "/register", "/css/**", "/js/**" , "/assets/**")
                .permitAll()
                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard" , true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout")
                .permitAll())
                .sessionManagement(session -> session.sessionFixation().migrateSession().maximumSessions(1)
                .expiredUrl("/login?expired"));
        return http.build();
    }
}