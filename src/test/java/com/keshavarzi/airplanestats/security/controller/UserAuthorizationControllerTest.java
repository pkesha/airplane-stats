package com.keshavarzi.airplanestats.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidUsernameException;
import com.keshavarzi.airplanestats.security.exception.register.UserAlreadyExistsException;
import com.keshavarzi.airplanestats.security.model.request.LoginRequest;
import com.keshavarzi.airplanestats.security.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.service.UserAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserAuthorizationController.class)
class UserAuthorizationControllerTest {
  private static final String BASE_AUTHORIZATION_URL = "/api/user/authorization";
  private static final String REGISTER_URL = "/register";
  private static final String LOGIN_URL = "/login";
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean UserAuthorizationService userAuthorizationService;
  @MockBean AuthenticationManager authenticationManager;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  /**
   * Helper function to parse object to JSON.
   *
   * @param object any object to parse
   * @return object in JSON String
   * @throws JsonProcessingException issue processing into JSON
   */
  private String mapFromJson(Object object) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }

  /**
   * Creates a RegisterRequest Object.
   *
   * @param username username of user
   * @param password Password
   * @return created RegisterRequest object for test/mocks
   */
  private RegisterRequest createRegisterRequest(String username, String password) {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername(username);
    registerRequest.setPassword(password);
    return registerRequest;
  }

  private LoginRequest createLoginRequest(String username, String password) {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername(username);
    loginRequest.setPassword(password);
    return loginRequest;
  }

  /**
   * Creates UserEntity database Object.
   *
   * @param username user's username
   * @param password user's password
   * @return created UserEntity object for test/mocks
   */
  private UserEntity createUserEntity(String username, String password) {
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(1L);
    userEntity.setUsername(username);
    userEntity.setPassword(password);
    return userEntity;
  }

  @Test
  void register_invalidUserUsernameAddressValidPassword_406() throws Exception {
    // Given
    String username = "registerInvalidUserUsernameAddressWith406";
    String password = "validPass";
    RegisterRequest registerRequest = this.createRegisterRequest(username, password);

    // When
    Mockito.when(this.userAuthorizationService.register(username, password))
        .thenThrow(InvalidUsernameException.class);

    // When & then
    this.mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                .content(this.mapFromJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @Test
  void register_withInvalidPasswordWithButValidUsername_406() throws Exception {
    // Given
    String username = "registerWithInvalidPasswordWithButValidUsername406@test.com";
    String password = "";
    RegisterRequest registerRequest = this.createRegisterRequest(username, password);

    // When
    Mockito.when(this.userAuthorizationService.register(username, password))
        .thenThrow(InvalidPasswordException.class);

    // When & then
    this.mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                .content(this.mapFromJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @Test
  void register_userWithValidUsernameRoleMissingValidPassword_404() throws Exception {
    String username = "registerUserWithValidUsernameRoleMissingValidPasswordWith404@test.com";
    String password = "validPass";
    RegisterRequest registerRequest = this.createRegisterRequest(username, password);

    // When
    Mockito.when(this.userAuthorizationService.register(username, password))
        .thenThrow(AuthorizationRoleMissingException.class);

    // When Then
    this.mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                .content(this.mapFromJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void register_existingUserValidPassword_409() throws Exception {
    // Given
    String username = "registerExistingUserValidPasswordWith409@test.com";
    String password = "validPass";
    RegisterRequest registerRequest = this.createRegisterRequest(username, password);

    // When
    Mockito.when(this.userAuthorizationService.register(username, password))
        .thenThrow(UserAlreadyExistsException.class);

    // Then
    mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                .content(this.mapFromJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  void successfulRegister_201() throws Exception {
    String username = "successfulRegister@test.com";
    String password = "password";
    RegisterRequest registerRequest = this.createRegisterRequest(username, password);
    UserEntity userEntity = this.createUserEntity(username, password);

    // When
    Mockito.when(this.userAuthorizationService.register(username, password)).thenReturn(userEntity);

    // When Then
    mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                .content(this.mapFromJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void unsuccessfulLogin_NonexistentUsernameValidPassword() throws Exception {
    String username = "dne@test.com";
    String password = "validPass";
    LoginRequest loginRequest = this.createLoginRequest(username, password);

    Mockito.when(this.userAuthorizationService.login(username, password))
        .thenThrow(UsernameNotFoundException.class);

    mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                .content(this.mapFromJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void unsuccessfulLogin_invalidValidUsernameValidPassword() throws Exception {
    String username = "unsuccessfulLoginInvalidValidUsernamePassword@test.com";
    String password = "invalidPass";
    LoginRequest loginRequest = this.createLoginRequest(username, password);

    Mockito.when(this.userAuthorizationService.login(username, password))
        .thenThrow(RuntimeException.class);

    mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                .content(this.mapFromJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void successfulLogin() throws Exception {
    String username = "successfulLogin@test.com";
    String password = "password";
    LoginRequest loginRequest = this.createLoginRequest(username, password);
    AuthorizationResponse authorizationResponse = new AuthorizationResponse();
    authorizationResponse.setAccessToken("ValidToken");

    Mockito.when(this.userAuthorizationService.login(username, password))
        .thenReturn(authorizationResponse);

    mockMvc
        .perform(
            post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                .content(this.mapFromJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
