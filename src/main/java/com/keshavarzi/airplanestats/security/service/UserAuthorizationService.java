package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.EmailAlreadyExistsException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.jwt.JwtUtility;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Authorization logic is implemented in service. */
@Service
public class UserAuthorizationService {

  @Nonnull
  private static final Pattern VALID_EMAIL_ADDRESS =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  @Nonnull private final AuthenticationManager authenticationManager;
  @Nonnull private final UserEntityRepository userEntityRepository;
  @Nonnull private final RoleEntityRepository roleEntityRepository;
  @Nonnull private final PasswordEncoder passwordEncoder;
  @Nonnull private final JwtUtility jwtUtility;

  @Autowired
  protected UserAuthorizationService(
      @Nonnull final AuthenticationManager authenticationManager,
      @Nonnull final UserEntityRepository userEntityRepository,
      @Nonnull final RoleEntityRepository roleEntityRepository,
      @Nonnull final PasswordEncoder passwordEncoder,
      @Nonnull final JwtUtility jwtUtility) {
    this.authenticationManager = authenticationManager;
    this.userEntityRepository = userEntityRepository;
    this.roleEntityRepository = roleEntityRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtility = jwtUtility;
  }

  /**
   * Registers a new user if criteria are met.
   *
   * @param userEmail User's email
   * @param password User's password
   * @throws InvalidPasswordException Invalid password length
   * @throws InvalidEmailException Invalid Email formatting
   * @throws EmailAlreadyExistsException Email already exists in database
   *     (plane_stats.user_data.user)
   * @throws AuthorizationRoleMissingException Authorization role does not exist in database
   *     (plane_stats.user_data.role)
   */
  public final UserEntity register(@NonNull final String userEmail, @NonNull final String password)
      throws InvalidPasswordException,
          InvalidEmailException,
          EmailAlreadyExistsException,
          AuthorizationRoleMissingException {
    if (!VALID_EMAIL_ADDRESS.matcher(userEmail).matches()) {
      throw new InvalidEmailException(userEmail);
    } else if (!((password.length() >= 8) && (password.length() <= 15))) {
      throw new InvalidPasswordException("Invalid password length");
    } else if (this.userEntityRepository.findUserEntityByEmail(userEmail).isPresent()) {
      throw new EmailAlreadyExistsException(userEmail);
    } else if (this.roleEntityRepository.findRoleEntityByRoleName("USER").isEmpty()) {
      throw new AuthorizationRoleMissingException("USER");
    } else {
      UserEntity userEntity = new UserEntity();
      userEntity.setEmail(userEmail);
      userEntity.setPassword(this.passwordEncoder.encode(password));
      RoleEntity roles = this.roleEntityRepository.findRoleEntityByRoleName("USER").get();

      userEntity.setRoleEntities(Collections.singletonList(roles));
      return this.userEntityRepository.save(userEntity);
    }
  }

  /**
   * Login's in a user if criteria is met and @code{AuthorizationResponse}.
   *
   * @param email user email for login
   * @param password user password for login
   * @return @code{AuthorizationResponse} will encapsulate authorization response
   */
  public final AuthorizationResponse login(
      @NonNull final String email, @NonNull final String password) {
    if (this.userEntityRepository.findUserEntityByEmail(email).isEmpty()) {
      throw new UsernameNotFoundException(email + "not found");
    } else {
      Authentication authentication =
          this.authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = this.jwtUtility.generateToken(authentication);
      AuthorizationResponse authorizationResponse = new AuthorizationResponse();
      authorizationResponse.setAccessToken(token);
      return authorizationResponse;
    }
  }
}
