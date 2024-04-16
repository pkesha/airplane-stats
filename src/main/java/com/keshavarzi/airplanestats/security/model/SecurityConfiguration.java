package com.keshavarzi.airplanestats.security.model;

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
     * <p> Security filter for endpoints, certain endpoints require certain users</p>
     * <p> Disable CSRF if it's being used as a backend application</p>
     * <p> Enable CSRF if project is being used with front end package</p>
     * @param http with SecurityFilterChain
     * @return url with security filter chain and headers
     * @throws Exception if things go wrong
     */
    @Bean
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
     * @param authenticationConfiguration Authentication Configuration object to make into a bean
     * @return AuthenticationManager bean
     * @throws Exception throws exception if things don't work??
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * <p>Create password encoder bean</p>
     * @return PasswordEncoder bean
     */
    @Bean(name = "PasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
