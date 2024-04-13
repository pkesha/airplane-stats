package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserDetailsServiceImpl.class)
class UserDetailsServiceImplTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @MockBean
    UserEntityRepository userEntityRepository;
    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        this.userDetailsService = new UserDetailsServiceImpl(this.userEntityRepository);
    }

    @Test()
    void loadUserByUsernameThatDoesNotExist() {
        String email = "loadUserByUsernameThatDoesNotExist@test.com";
        String exceptionMessage = "Email: " + email + " was not found.";


        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty())
                .thenThrow(new UsernameNotFoundException(exceptionMessage));

        assertThrows(UsernameNotFoundException.class,
                () -> this.userDetailsService.loadUserByUsername(email));
    }

    @Test
    void failToMapRolesToAuthorities() {
        String email = "failToMapRolesToAuthorities@test.com";
        String exceptionMessage = "Error adding roles to user: JUNIT TEST";

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(this.)
                .thenThrow(new UsernameNotFoundException(exceptionMessage));
    }

}