package com.keshavarzi.airplanestats.security.model;

import com.keshavarzi.airplanestats.security.jwt.JwtAuthenticationEntryPoint;
import com.keshavarzi.airplanestats.security.jwt.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configure & enable security settings for SpringBoot service.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration {
  private static final String AUTHORIZED_URL = "/api/authorization/**";
  private JwtAuthenticationEntryPoint authenticationEntryPoint;

  /**
   * Security filter for endpoints, certain endpoints require certain users.
   *
   * <p>Disable CSRF if it's being used as a backend application
   *
   * <p>Enable CSRF if project is being used with front end package
   *
   * @param http with SecurityFilterChain
   * @return url with security filter chain and headers
   * @throws Exception if things go wrong
   */
  @Bean
  public SecurityFilterChain securityFilterChain(@NonNull final HttpSecurity http)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            (exceptionHandlingConfigurer) ->
                exceptionHandlingConfigurer.authenticationEntryPoint(this.authenticationEntryPoint))
        .sessionManagement(
            (securitySessionManagementConfigurer) ->
                securitySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            (authorize) ->
                authorize.requestMatchers(AUTHORIZED_URL).permitAll().anyRequest().permitAll())
        .httpBasic(Customizer.withDefaults());
    http.addFilterBefore(
        this.jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Returns bean of the JWTAuthentication filter.
   *
   * @return JwtAuthenticationFilter bean of the JWTAuthentication filter
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  /**
   * Will create the authentication manager bean.
   *
   * @param authenticationConfiguration Authentication Configuration object to make into a bean
   * @return AuthenticationManager bean
   * @throws Exception throws exception if things don't work??
   */
  @Bean
  public AuthenticationManager authenticationManager(
      @NonNull final AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * Create password encoder bean.
   *
   * @return PasswordEncoder bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
