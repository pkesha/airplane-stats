package com.keshavarzi.airplanestats.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailExistException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.model.request.LoginRequest;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.security.service.UserAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserAuthorizationController.class)
class UserAuthorizationControllerTest {
    private static final String BASE_AUTHORIZATION_URL = "/api/user/authorization";
    private static final String REGISTER_URL = "/register";
    private static final String LOGIN_URL = "/login";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @MockBean
    UserAuthorizationService userAuthorizationService;
    @MockBean
    AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    /**
     * Helper function to parse object to JSON
     * @param object: any object to parse
     * @return object in JSON String
     * @throws JsonProcessingException: issue processing into JSON
     */
    private String mapFromJson(Object object)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    /**
     * Creates a RegisterRequest Object
     * @param email: email of user
     * @param password: Password
     * @return created RegisterRequest object for test/mocks
     */
    private RegisterRequest createRegisterRequest(String email, String password) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        return registerRequest;
    }

    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return loginRequest;
    }

    /**
     * Creates UserEntity database Object
     * @param email: user's email
     * @param password: user's password
     * @return created UserEntity object for test/mocks
     */
    private UserEntity createUserEntity(String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        return userEntity;
    }

    @Test
    void registerInvalidUserEmailAddressWith406() throws Exception {
        // Given
        String email = "registerInvalidUserEmailAddressWith406";
        String password = "password";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userAuthorizationService.register(email, password))
                .thenThrow(InvalidEmailException.class);

        // When & then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());

    }

    @Test
    void registerWithInvalidPasswordWith406() throws Exception {
        // Given
        String email = "registerWithInvalidPassword@test.com";
        String password = "";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userAuthorizationService.register(email, password))
                        .thenThrow(InvalidPasswordException.class);

        // When & then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    void registerUserWithUserRoleMissingWith404() throws Exception {
        String email = "registerUserWithUserRoleMissing@test.com";
        String password = "password";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userAuthorizationService.register(email, password))
                        .thenThrow(AuthorizationRoleMissingException.class);

        // When Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void registerExistingUserValidEmailAddressWith409() throws Exception {
        // Given
        String email = "registerExistingUserValidEmailAddress@test.com";
        String password = "password";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userAuthorizationService.register(email, password))
                .thenThrow(EmailExistException.class);

        // Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void successfulRegisterWith201() throws Exception {
        String email = "successfulRegister@test.com";
        String password = "password";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);
        UserEntity userEntity = this.createUserEntity(email, password);

        // When
        Mockito.when(this.userAuthorizationService.register(email, password))
                        .thenReturn(userEntity);

        // When Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void unsuccessfulLoginInvalidEmail() throws Exception {
        String email = "dne@test.com";
        String password = "validPass";
        LoginRequest loginRequest = this.createLoginRequest(email, password);
        AuthorizationResponse emptyAuthorizationResponse = new AuthorizationResponse();

        Mockito.when(this.userAuthorizationService.login(email, password))
                .thenReturn(emptyAuthorizationResponse)
                .thenThrow(UsernameNotFoundException.class);

        mockMvc.perform(post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                .content(this.mapFromJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void unsuccessfulLoginInvalidPassword() throws Exception {
        String email = "unsuccessfulLoginInvalidPassword@test.com";
        String password = "invalidPass";
        AuthorizationResponse emptyAuthorizationResponse = new AuthorizationResponse();
        LoginRequest loginRequest = this.createLoginRequest(email, password);

        Mockito.when(this.userAuthorizationService.login(email, password))
                .thenReturn(emptyAuthorizationResponse)
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                        .content(this.mapFromJson(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void successfulLogin() throws Exception {
        String email = "successfulLogin@test.com";
        String password = "password";
        LoginRequest loginRequest = this.createLoginRequest(email, password);
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        authorizationResponse.setAccessToken("ValidToken");

        Mockito.when(this.userAuthorizationService.login(email, password))
                        .thenReturn(authorizationResponse);

        mockMvc.perform(post(BASE_AUTHORIZATION_URL + LOGIN_URL)
                        .content(this.mapFromJson(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}