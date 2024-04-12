package com.keshavarzi.airplanestats.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    //Object from Json
    private String mapFromJson(Object object)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @Test
    void registerInvalidEmailAddress() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("invalidemail");
        registerRequest.setPassword("password");

        // When & then
        mockMvc.perform(post(BASE_AUTHORIZATION_URL + REGISTER_URL)
                        .content(this.mapFromJson(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());

    }

//    @Test
//    void registerExistingEmailAddress() throws Exception {
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("test@test.com");
//        registerRequest.setPassword("password");
//
//
//    }
}