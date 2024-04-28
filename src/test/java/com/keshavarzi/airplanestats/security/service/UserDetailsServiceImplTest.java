package com.keshavarzi.airplanestats.security.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserDetailsServiceImpl.class)
class UserDetailsServiceImplTest {
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean UserEntityRepository userEntityRepository;
  @MockBean PasswordEncoder passwordEncoder;
  @MockBean RoleEntityRepository roleEntityRepository;
  @Autowired UserDetailsServiceImpl userDetailsService;

  @BeforeEach
  void setUp() {
    this.userDetailsService = new UserDetailsServiceImpl(this.userEntityRepository);
  }

  @Test
  void loadUserByUsernameThatDoesNotExist() {
    String email = "loadUserByUsernameThatDoesNotExist@test.com";

    Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
        .thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> this.userDetailsService.loadUserByUsername(email));
  }

  // TODO: Successful Junit test - keep commented code but it returns an exception when it shouldn't
}
