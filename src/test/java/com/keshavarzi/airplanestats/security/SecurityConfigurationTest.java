package com.keshavarzi.airplanestats.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SecurityConfiguration.class)
@ExtendWith(SpringExtension.class)
class SecurityConfigurationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    AuthenticationConfiguration authenticationConfiguration;
    @Test
    void givenIncorrectURL_Return404() throws Exception {
        this.mockMvc.perform(get("/data/actuator/health/liveness"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnPasswordEncoder() {
        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
        Assertions.assertNotNull(securityConfiguration.passwordEncoder());
    }

    //TODO: returnAuthenticationManager Junit test
    @Test
    void returnAuthenticationManager() {
//        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
//        Mockito.when(securityConfiguration.authenticationManager(this.authenticationConfiguration))
//                        .thenReturn((AuthenticationManager) new Object());
    }
}