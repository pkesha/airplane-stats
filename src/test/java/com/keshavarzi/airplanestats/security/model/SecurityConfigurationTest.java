package com.keshavarzi.airplanestats.security.model;


import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.jwt.JwtAuthenticationEntryPoint;
import com.keshavarzi.airplanestats.security.jwt.JwtUtility;
import com.keshavarzi.airplanestats.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

// TODO: Junit test
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = SecurityConfiguration.class)
@ExtendWith(SpringExtension.class)
class SecurityConfigurationTest {
  @MockBean AuthenticationConfiguration authenticationConfiguration;
  @MockBean UserEntityRepository userEntityRepository;
  @MockBean JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean JwtUtility jwtUtility;
  @MockBean UserDetailsServiceImpl userDetailsService;
  @Autowired WebApplicationContext webApplicationContext;
  @Autowired SecurityConfiguration securityConfiguration;
}
