package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidUsernameException;
import com.keshavarzi.airplanestats.security.exception.register.UserAlreadyExistsException;
import com.keshavarzi.airplanestats.security.jwt.JwtSecurityConstants;
import com.keshavarzi.airplanestats.security.jwt.JwtUtility;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.model.response.RegistrationResponse;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  @Nonnull private final JwtUtility jwtUtility;

  @Autowired
  protected UserAuthorizationService(
      @Nonnull final AuthenticationManager authenticationManager,
      @Nonnull final UserEntityRepository userEntityRepository,
      @Nonnull final RoleEntityRepository roleEntityRepository,
      @Nonnull final JwtUtility jwtUtility) {
    this.authenticationManager = authenticationManager;
    this.userEntityRepository = userEntityRepository;
    this.roleEntityRepository = roleEntityRepository;
    this.jwtUtility = jwtUtility;
  }

  /**
   * Registers a new user if criteria are met.
   *
   * @param username User's username
   * @param password User's password
   * @throws InvalidPasswordException Invalid password length
   * @throws InvalidUsernameException Invalid Username formatting
   * @throws UserAlreadyExistsException Email already exists in database
   *     (plane_stats.user_data.user)
   * @throws AuthorizationRoleMissingException Authorization role does not exist in database
   *     (plane_stats.user_data.role)
   */
  public final RegistrationResponse register(
      @NonNull final String username, @NonNull final String password)
      throws InvalidPasswordException,
          InvalidUsernameException,
          UserAlreadyExistsException,
          AuthorizationRoleMissingException {
    if (!VALID_EMAIL_ADDRESS.matcher(username).matches()) {
      throw new InvalidUsernameException(username);
    } else if (!((password.length() >= 8) && (password.length() <= 15))) {
      throw new InvalidPasswordException("Invalid password length");
    } else if (this.userEntityRepository.findUserEntityByUsername(username).isPresent()) {
      throw new UserAlreadyExistsException(username);
    } else if (this.roleEntityRepository.findRoleEntityByRoleName("USER").isEmpty()) {
      throw new AuthorizationRoleMissingException("USER");
    } else {
      final RoleEntity roles = this.roleEntityRepository.findRoleEntityByRoleName("USER").get();
      final UserEntity userEntity = new UserEntity(username, password, List.of(roles));
      this.userEntityRepository.save(userEntity);
      return new RegistrationResponse("User created: " + username);
    }
  }

  /**
   * Login's in a user if criteria is met with @code{AuthorizationResponse}.
   *
   * @param username user username for login
   * @param password user password for login
   * @return @code{AuthorizationResponse} will encapsulate authorization response
   */
  public final AuthorizationResponse login(
      @NonNull final String username, @NonNull final String password) {
    this.userEntityRepository
        .findUserEntityByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    final Authentication authentication =
        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final String token = this.jwtUtility.generateToken(authentication);
    return new AuthorizationResponse(token, JwtSecurityConstants.TOKEN_PREFIX_BEARER, null);
  }
}
