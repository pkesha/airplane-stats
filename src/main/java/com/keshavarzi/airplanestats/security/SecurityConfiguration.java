package com.keshavarzi.airplanestats.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String AUTHORIZED_URL = "/api/authorization/**";

    /**
     * Security filter for endpoints, certain endpoints require certain users
     * Disable CSRF if it's being used as a backend application
     * Enable CSRF if project is being used with front end package
     *
     * @param http contains url endpoint
     * @return url with security filter chain and headers
     * @throws Exception if things go wrong
     */
    @Bean(name = "SecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(AUTHORIZED_URL)
                        .permitAll()
                        .anyRequest()
                        .permitAll())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Will create the authentication manager bean
     * @param authenticationConfiguration: Authentication Configuration object to make into a bean
     * @return bean of configuration manager
     * @throws Exception throws exception if things don't work??
     */
    @Bean(name = "AuthenticationManager")
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Create password encoder bean
     * @return password encoder bean
     */
    @Bean(name = "PasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
