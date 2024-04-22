package com.keshavarzi.airplanestats.security.model;

import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.jwt.JwtAuthenticationEntryPoint;
import com.keshavarzi.airplanestats.security.jwt.JwtGenerator;
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
    @MockBean
    UserEntityRepository userEntityRepository;
    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    JwtGenerator jwtGenerator;

    @Test
    void givenIncorrectURL_Return404() throws Exception {
        this.mockMvc.perform(get("/data/actuator/health/liveness"))
                .andExpect(status().isNotFound());
    }

    //TODO: returnAuthenticationManager Junit test
    @Test
    void returnAuthenticationManager() {
//        AuthenticationManager authenticationManager =
//                new ProviderManager((AuthenticationProvider) authenticationConfiguration);
//        notNull(this.securityConfiguration.authenticationManager(authenticationConfiguration));
    }
}