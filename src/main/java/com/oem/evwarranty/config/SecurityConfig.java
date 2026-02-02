package com.oem.evwarranty.config;

import com.oem.evwarranty.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security Configuration for role-based access control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Swagger API Docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // REST API endpoints (require authentication)
                        .requestMatchers("/api/v1/**").authenticated()

                        // Service Center routes (SC_STAFF and SC_TECHNICIAN)
                        .requestMatchers("/sc/**").hasAnyRole("SC_STAFF", "SC_TECHNICIAN", "ADMIN")

                        // EVM (Manufacturer) routes
                        .requestMatchers("/evm/**").hasAnyRole("EVM_STAFF", "ADMIN")

                        // Admin routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Dashboard accessible to all authenticated users
                        .requestMatchers("/dashboard/**").authenticated()

                        // Reports accessible to all roles
                        .requestMatchers("/reports/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403"))
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        // Allow H2 console for development
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect springSecurityDialect() {
        return new org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect();
    }
}
