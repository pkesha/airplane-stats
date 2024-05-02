package com.keshavarzi.airplanestats.security.controller;

import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidUsernameException;
import com.keshavarzi.airplanestats.security.exception.register.UserAlreadyExistsException;
import com.keshavarzi.airplanestats.security.model.request.LoginRequest;
import com.keshavarzi.airplanestats.security.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.model.response.RegistrationResponse;
import com.keshavarzi.airplanestats.security.service.UserAuthorizationService;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rest Controller to register, login and other authorizations. */
@RestController
@RequestMapping(path = "/api/user/authorization", name = "UserAuthorizationController")
final class UserAuthorizationController {
  @Nonnull private final UserAuthorizationService userAuthorizationService;

  @Autowired
  public UserAuthorizationController(
      @Nonnull final UserAuthorizationService userAuthorizationService) {
    this.userAuthorizationService = userAuthorizationService;
  }

  /**
   * Register API for a new user with username and password.
   *
   * @param registerRequest DTO to create user in database table ({@code
   *     plane_stats.user_data.user})
   * @return {@code HttpStatus.NOT_ACCEPTABLE}: Invalid {@code username} or {@code password}
   *     <p>{@code HttpStatus.CONFLICT}: {@code username} exists in database table ({@code
   *     plane_stats.user_data.user})</p>
   *     <p>{@code HttpStatus.NOT_FOUND}: Spring Security role not present in database table ({@code
   *     plane_stats.user_data.role}). Roles should exist in database at all times.</p>
   *     <p>{@code HttpStatus.CREATED}: {@code UserEntity} has been created with encoded password &
   *     stored in database ({@code plane_stats.user_data.user}) as a Spring security user</p>
   */
  @PostMapping(
      path = "register",
      name = "RegisterUser",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RegistrationResponse> register(
      @NonNull @RequestBody final RegisterRequest registerRequest) {
    String username = registerRequest.username();
    try {
      return new ResponseEntity<>(
          this.userAuthorizationService.register(username, registerRequest.password()),
          HttpStatus.CREATED);
    } catch (InvalidUsernameException | InvalidPasswordException invalidCredentials) {
      return new ResponseEntity<>(
          new RegistrationResponse(invalidCredentials.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    } catch (UserAlreadyExistsException userAlreadyExistsException) {
      return new ResponseEntity<>(
          new RegistrationResponse(userAlreadyExistsException.getMessage()), HttpStatus.CONFLICT);
    } catch (AuthorizationRoleMissingException authorizationRoleMissingException) {
      return new ResponseEntity<>(
          new RegistrationResponse(authorizationRoleMissingException.getMessage()),
          HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Login API. Will catch Exceptions for Unauthorized or unknown username login attempts.
   *
   * @param loginRequest DTO containing username and password
   * @return Response entity encapsulating {@code AuthorizationResponse} DTO. DTO informs of
   *     authorization results.
   */
  @PostMapping(path = "login", name = "UserLogin", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthorizationResponse> login(
      @NonNull @RequestBody final LoginRequest loginRequest) {
    try {
      return new ResponseEntity<>(
          this.userAuthorizationService.login(loginRequest.username(), loginRequest.password()),
          HttpStatus.OK);
    } catch (RuntimeException usernameNotFoundException) {
      return new ResponseEntity<>(
          new AuthorizationResponse(null, null, usernameNotFoundException.getMessage()),
          HttpStatus.NOT_FOUND);
    }
  }
}
