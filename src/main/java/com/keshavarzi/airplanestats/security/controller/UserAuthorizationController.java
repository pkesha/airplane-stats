package com.keshavarzi.airplanestats.security.controller;

import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.EmailAlreadyExistsException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
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
   * Register a new user with email and password.
   *
   * @param registerRequest DTO to create user
   * @return HttpStatus.NOT_ACCEPTABLE: Invalid username or password
   *     <p>{@code HttpStatus.CONFLICT}: Existing Email in database (plane_stats.user_data.user)</p>
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
    String email = registerRequest.getEmail();
    try {
      this.userAuthorizationService.register(email, registerRequest.getPassword());
      return new ResponseEntity<>(
          "User with email " + email + " has been created", HttpStatus.CREATED);
    } catch (InvalidEmailException | InvalidPasswordException invalidCredentials) {
      return new ResponseEntity<>(invalidCredentials.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    } catch (EmailAlreadyExistsException emailAlreadyExistsException) {
      return new ResponseEntity<>(emailAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    } catch (AuthorizationRoleMissingException authorizationRoleMissingException) {
      return new ResponseEntity<>(
          authorizationRoleMissingException.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Login API. Will catch Exceptions for Unauthorized or unknown email login attempts.
   *
   * @param loginRequest DTO containing email and password
   * @return Response entity encapsulating {@code AuthorizationResponse} DTO. DTO informs of
   *     authorization results.
   */
  @PostMapping(
      path = "login",
      name = "UserLogin",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthorizationResponse> login(
      @NonNull @RequestBody final LoginRequest loginRequest) {
    AuthorizationResponse authorizationResponse = new AuthorizationResponse();
    try {
      authorizationResponse =
          this.userAuthorizationService.login(loginRequest.getEmail(), loginRequest.getPassword());
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
