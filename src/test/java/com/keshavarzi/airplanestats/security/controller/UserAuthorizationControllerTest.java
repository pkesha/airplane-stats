package com.keshavarzi.airplanestats.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.security.exception.register.InvalidUsernameException;
import com.keshavarzi.airplanestats.security.exception.register.UserAlreadyExistsException;
import com.keshavarzi.airplanestats.security.jwt.JwtSecurityConstants;
import com.keshavarzi.airplanestats.security.model.request.LoginRequest;
import com.keshavarzi.airplanestats.security.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.security.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.model.response.RegistrationResponse;
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
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }

  @Test
  void register_invalidUserUsernameAddressValidPassword_406() throws Exception {
    // Given
    final String username = "registerInvalidUserUsernameAddressWith406";
    final String password = "validPass";
    final RegisterRequest registerRequest = new RegisterRequest(username, password);

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
    final String username = "registerWithInvalidPasswordWithButValidUsername406@test.com";
    final String password = "";
    final RegisterRequest registerRequest = new RegisterRequest(username, password);

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
    final String username = "registerUserWithValidUsernameRoleMissingValidPasswordWith404@test.com";
    final String password = "validPass";
    final RegisterRequest registerRequest = new RegisterRequest(username, password);

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
    final String username = "registerExistingUserValidPasswordWith409@test.com";
    final String password = "validPass";
    final RegisterRequest registerRequest = new RegisterRequest(username, password);

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
    final String username = "successfulRegister@test.com";
    final String password = "password";
    final RegisterRequest registerRequest = new RegisterRequest(username, password);
    final RegistrationResponse response = new RegistrationResponse("Created");
    // When
    Mockito.when(this.userAuthorizationService.register(username, password)).thenReturn(response);

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
    final String username = "dne@test.com";
    final String password = "validPass";
    final LoginRequest loginRequest = new LoginRequest(username, password, null);

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
  void successfulLogin() throws Exception {
    final String username = "successfulLogin@test.com";
    final String password = "password";
    final LoginRequest loginRequest = new LoginRequest(username, password, null);
    final AuthorizationResponse authorizationResponse =
        new AuthorizationResponse("ValidToken", JwtSecurityConstants.TOKEN_PREFIX_BEARER, null);

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
