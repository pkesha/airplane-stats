package com.keshavarzi.airplanestats.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/** Tests {@code UserAuthorizationService}. */
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserAuthorizationService.class)
public class UserAuthorizationServiceTest {
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean UserEntityRepository userEntityRepository;
  @MockBean RoleEntityRepository roleEntityRepository;
  @MockBean AuthenticationManager authenticationManager;
  @MockBean PasswordEncoder passwordEncoder;
  @MockBean JwtUtility jwtUtility;
  @Autowired UserAuthorizationService userAuthorizationService;

  @BeforeEach
  void setUp() {
    this.userAuthorizationService =
        new UserAuthorizationService(
            this.authenticationManager,
            this.userEntityRepository,
            this.roleEntityRepository,
            this.passwordEncoder,
            this.jwtUtility);
  }

  /**
   * Creates {@code UserEntity} for testing.
   *
   * @param username User username
   * @param password user password
   * @return UserEntity object
   */
  private UserEntity createUserEntity(String username, String password) {
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);
    userEntity.setUsername(username);
    userEntity.setPassword(password);

    return userEntity;
  }

  private RoleEntity createUserRoleEntity() {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setRoleId(3L);
    roleEntity.setRoleName("USER");
    return roleEntity;
  }

  @Test
  void registerWithInvalidUsername() {
    final String username = "registerWithInvalidUsername";
    final String password = "password";

    assertThrows(
        InvalidUsernameException.class,
        () -> this.userAuthorizationService.register(username, password));
  }

  @Test
  void registerWithInvalidPasswordLessThan8Characters() {
    final String username = "registerWithInvalidPasswordLessThan8Characters@test.com";
    final String password = "test";

    assertThrows(
        InvalidPasswordException.class,
        () -> this.userAuthorizationService.register(username, password));
  }

  @Test
  void registerWithInvalidPasswordMoreThan15Characters() {
    final String username = "registerWithInvalidPasswordMoreThan15Characters@test.com";
    final String password = "registerWithInvalidPasswordMoreThan15Characters";

    assertThrows(
        InvalidPasswordException.class,
        () -> this.userAuthorizationService.register(username, password));
  }

  @Test
  void registerWithExistingUsername() {
    final String username = "validUsernameTest@test.com";
    final String password = "validPassword";
    final UserEntity userEntity = this.createUserEntity(username, password);

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.of(userEntity));

    assertThrows(
        UserAlreadyExistsException.class,
        () -> this.userAuthorizationService.register(username, password));
  }

  @Test
  void registerWithMissingAuthorizationRole() {
    final String username = "validUsernameTest@test.com";
    final String password = "validPassword";

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.empty());
    Mockito.when(this.passwordEncoder.encode(password)).thenReturn("encodedPass");

    assertThrows(
        AuthorizationRoleMissingException.class,
        () -> this.userAuthorizationService.register(username, password));
  }

  @Test
  void successfulRegistration()
      throws UserAlreadyExistsException,
          InvalidPasswordException,
          AuthorizationRoleMissingException,
          InvalidUsernameException {
    final String username = "successfulRegistration@test.com";
    final String password = "validPassword";

    final RegistrationResponse registrationResponse =
        new RegistrationResponse("User created: " + username);
    final UserEntity userEntity = new UserEntity();
    final RoleEntity roleEntity = this.createUserRoleEntity();

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.empty());
    Mockito.when(this.roleEntityRepository.findRoleEntityByRoleName("USER"))
        .thenReturn(Optional.of(roleEntity));
    Mockito.when(this.passwordEncoder.encode(password)).thenReturn("encodedPass");
    Mockito.when(this.userEntityRepository.save(Mockito.any(UserEntity.class)))
        .thenReturn(userEntity);

    assertEquals(registrationResponse, this.userAuthorizationService.register(username, password));
  }

  @Test
  void loginFailedUsernameNotFound() {
    final String username = "usernameDoesNotExist@test.com";
    final String password = "validPass";

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> this.userAuthorizationService.login(username, password));
  }

  @Test
  void loginFailedAuthentication() {
    final String username = "loginFailedAuthentication@test.com";
    final String password = "invalidPass";

    UserEntity userEntity = this.createUserEntity(username, password);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(username, password);

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.of(userEntity));
    Mockito.when(this.authenticationManager.authenticate(usernamePasswordAuthenticationToken))
        .thenThrow(new RuntimeException());

    assertThrows(
        RuntimeException.class, () -> this.userAuthorizationService.login(username, password));
  }

  @Test
  void successfulLogin() {
    final String username = "loginFailedAuthentication@test.com";
    final String password = "invalidPass";
    final String token = "token";
    final UserEntity userEntity = this.createUserEntity(username, password);
    final TestingAuthenticationToken authentication =
        new TestingAuthenticationToken(username, password);
    final AuthorizationResponse expectedAuthorizationResponse =
        new AuthorizationResponse(token, JwtSecurityConstants.TOKEN_PREFIX_BEARER, null);

    Mockito.when(this.userEntityRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.of(userEntity));
    Mockito.when(this.authenticationManager.authenticate(authentication))
        .thenReturn(authentication);
    Mockito.when(this.jwtUtility.generateToken(authentication)).thenReturn(token);

    AuthorizationResponse actualAuthorizationResponse =
        this.userAuthorizationService.login(username, password);
    assertEquals(expectedAuthorizationResponse, actualAuthorizationResponse);
  }
}
