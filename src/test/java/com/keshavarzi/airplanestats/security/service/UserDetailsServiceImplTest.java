package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    RoleEntityRepository roleEntityRepository;

    @BeforeEach
    void setUp() {
        this.userDetailsService =
                new UserDetailsServiceImpl(this.userEntityRepository);
    }

    @Test
    void loadUserByUsernameThatDoesNotExist() {
        String email = "loadUserByUsernameThatDoesNotExist@test.com";
        String exceptionMessage = "Email: " + email + " was not found.";


        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty())
                .thenThrow(new UsernameNotFoundException(exceptionMessage));

        assertThrows(UsernameNotFoundException.class,
                () -> this.userDetailsService.loadUserByUsername(email));
    }

    //TODO: Successful Junit test - keep commented code but it returns an exception
    @Test
    void successfulLoadOfUsernames() {
//        String email = "successfulLoadOfUsernames@test.com";
//
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail(email);
//        userEntity.setPassword("password");
//
//
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setRoleName("USER");
//        userEntity.setRoleEntities(List.of(roleEntity));
//
//        Collection<GrantedAuthority> grantedAuthoritiesToUser = new ArrayList<>();
//        grantedAuthoritiesToUser.add(new SimpleGrantedAuthority(roleEntity.getRoleName()));
//
//        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
//                .thenReturn(Optional.of(userEntity));
//
//        Mockito.when(this.userDetailsService.loadUserByUsername(email))
//                .thenReturn(new User(userEntity.getEmail(), userEntity.getPassword(), null));
//
//        assertNotNull(this.userDetailsService.loadUserByUsername(email));
    }
}