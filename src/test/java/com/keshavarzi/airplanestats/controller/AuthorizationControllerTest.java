package com.keshavarzi.airplanestats.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = AuthorizationController.class)
class AuthorizationControllerTest {
    private static final String BASE_AUTHORIZATION_URL = "/api/authorization";
    private static final String REGISTER_URL = "/register";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @MockBean
    UserEntityRepository userEntityRepository;
    @MockBean
    RoleEntityRepository roleEntityRepository;
    @MockBean
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    //Object from Json
    private String mapFromJson(Object object)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private RegisterRequest createRegisterRequest(String email, String password) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        return registerRequest;
    }

    private UserEntity createUserEntity(String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        return userEntity;
    }

    private RoleEntity createRoleEntity(String roleName) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(roleName);
        return roleEntity;
    }

    @Test
    void registerInvalidUserEmailAddress() throws Exception {
        // Given
        String email = "";
        String password = "password";
        String roleName = "USER";
        UserEntity userEntity = this.createUserEntity(email, password);
        RoleEntity roleEntity = this.createRoleEntity(roleName);
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(this.roleEntityRepository.findRoleEntityByRoleName(roleName))
                .thenReturn(Optional.of(roleEntity));

        // When & then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());

    }

    @Test
    void registerWithInvalidPassword() throws Exception {
        // Given
        String email = "registerWithInvalidPassword@test.com";
        String password = "";
        String roleName = "USER";
        UserEntity userEntity = this.createUserEntity(email, password);
        RoleEntity roleEntity = this.createRoleEntity(roleName);
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(roleEntityRepository.findRoleEntityByRoleName(roleName))
                .thenReturn(Optional.of(roleEntity));

        // When & then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    void registerUserWithUserRoleMissing() throws Exception {
        String email = "registerUserWithUserRoleMissing@test.com";
        String password = "password";
        String roleName = "USER";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty());
        Mockito.when(roleEntityRepository.findRoleEntityByRoleName(roleName))
                .thenReturn(Optional.empty());

        // When Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void registerExistingUserValidEmailAddress() throws Exception {
        // Given
        String email = "registerExistingUserValidEmailAddress@test.com";
        String password = "password";
        String roleName = "USER";
        UserEntity userEntity = this.createUserEntity(email, password);
        RoleEntity roleEntity = this.createRoleEntity(roleName);
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);

        // When
        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(roleEntityRepository.findRoleEntityByRoleName(roleName))
                .thenReturn(Optional.of(roleEntity));

        // Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void successfulRegister() throws Exception {
        String email = "successfulRegister@test.com";
        String password = "password";
        String roleName = "USER";
        RegisterRequest registerRequest = this.createRegisterRequest(email, password);
        RoleEntity roleEntity = this.createRoleEntity(roleName);


        // When
        Mockito.when(userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty());
        Mockito.when(roleEntityRepository.findRoleEntityByRoleName(roleName))
                .thenReturn(Optional.of(roleEntity));
        Mockito.when(this.passwordEncoder.encode(registerRequest.getPassword()))
                        .thenReturn("encodedPassword");

        // When Then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

}