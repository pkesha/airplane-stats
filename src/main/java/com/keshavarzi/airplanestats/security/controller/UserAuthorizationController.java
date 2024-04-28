package com.keshavarzi.airplanestats.security.controller;

import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidUsernameException;
import com.keshavarzi.airplanestats.security.exception.register.UserAlreadyExistsException;
import com.keshavarzi.airplanestats.security.model.request.LoginRequest;
import com.keshavarzi.airplanestats.security.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.service.UserAuthorizationService;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
   * Register a new user with username and password.
   *
   * @param registerRequest DTO to create user
   * @return HttpStatus.NOT_ACCEPTABLE: Invalid username or password
   *     <p>{@code HttpStatus.CONFLICT}:
   *     Existing username in database (plane_stats.user_data.user)</p>
   *     <p>{@code HttpStatus.NOT_FOUND}: 'USER' Spring security role not present in database</p>
   *     (plane_stats.user_data.role). Roles should exist in database at all time.
   *     <p>{@code HttpStatus.CREATED}: User has been created with encoded password & stored in</p>
   *     database (plane_stats.user_data.user) as a 'USER' Spring security user
   */
  @PostMapping(
      path = "register",
      name = "RegisterUser",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> register(
      @NonNull @RequestBody final RegisterRequest registerRequest) {
    String username = registerRequest.getUsername();
    try {
      this.userAuthorizationService.register(username, registerRequest.getPassword());
      return new ResponseEntity<>(
          "User with username " + username + " has been created", HttpStatus.CREATED);
    } catch (InvalidUsernameException | InvalidPasswordException invalidCredentials) {
      return new ResponseEntity<>(invalidCredentials.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    } catch (UserAlreadyExistsException userAlreadyExistsException) {
      return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    } catch (AuthorizationRoleMissingException authorizationRoleMissingException) {
      return new ResponseEntity<>(
          authorizationRoleMissingException.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Login API. Will catch Exceptions for Unauthorized or unknown username login attempts.
   *
   * @param loginRequest DTO containing username and password
   * @return Response entity encapsulating {@code AuthorizationResponse} DTO. DTO informs of
   *     authorization results.
   */
  @PostMapping(
      path = "login",
      name = "UserLogin",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthorizationResponse> login(
      @NonNull @RequestBody final LoginRequest loginRequest) {
    AuthorizationResponse authorizationResponse = new AuthorizationResponse();
    try {
      authorizationResponse = this.userAuthorizationService
            .login(loginRequest.getUsername(), loginRequest.getPassword());
    } catch (UsernameNotFoundException usernameNotFoundException) {
      authorizationResponse.setUnauthorizedError(usernameNotFoundException.getMessage());
      return new ResponseEntity<>(authorizationResponse, HttpStatus.NOT_FOUND);
    } catch (RuntimeException runtimeException) {
      authorizationResponse.setUnauthorizedError(runtimeException.getMessage());
      return new ResponseEntity<>(authorizationResponse, HttpStatus.UNAUTHORIZED);
    }
    return new ResponseEntity<>(authorizationResponse, HttpStatus.OK);
  }
}
