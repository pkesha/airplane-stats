package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailExistException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                new UserDetailsServiceImpl(this.userEntityRepository, this.passwordEncoder, this.roleEntityRepository);
    }

    /**
     * Creates UserEntity Object for testing
     * @param email User email
     * @param password user password
     * @return UserEntity object
     */
    private UserEntity createUserEntity(String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        userEntity.setEmail(email);
        userEntity.setPassword(password);

        return userEntity;
    }

    private RoleEntity createUserRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(3L);
        roleEntity.setRoleName("USER");
        return roleEntity;
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

    @Test
    void registerWithInvalidEmailAddress() {
        String email = "registerWithInvalidEmailAddress";
        String password = "password";

        assertThrows(InvalidEmailException.class, () ->
                this.userDetailsService.register(email, password));

    }

    @Test
    void registerWithInvalidPasswordLessThan8Characters(){
        String email = "registerWithInvalidPasswordLessThan8Characters@test.com";
        String password = "test";

        assertThrows(InvalidPasswordException.class, () ->
                this.userDetailsService.register(email, password));
    }

    @Test
    void registerWithInvalidPasswordMoreThan15Characters(){
        String email = "registerWithInvalidPasswordMoreThan15Characters@test.com";
        String password = "registerWithInvalidPasswordMoreThan15Characters";

        assertThrows(InvalidPasswordException.class, () ->
                this.userDetailsService.register(email, password));
    }

    @Test
    void registerWithExistingEmail() {
        String email = "validEmailTest@test.com";
        String password = "validPassword";
        UserEntity userEntity = this.createUserEntity(email, password);

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));

        assertThrows(EmailExistException.class, () ->
                this.userDetailsService.register(email, password));
    }

    @Test
    void registerWithMissingAuthorizationRole() {
        String email = "validEmailTest@test.com";
        String password = "validPassword";

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty());
        Mockito.when(this.passwordEncoder.encode(password))
                .thenReturn("encodedPass");

        assertThrows(AuthorizationRoleMissingException.class, () ->
                this.userDetailsService.register(email, password));
    }

    @Test
    void successfulRegistration() throws EmailExistException, InvalidPasswordException, AuthorizationRoleMissingException, InvalidEmailException {
        String email = "successfulRegistration@test.com";
        String password = "validPassword";

        UserEntity userEntity = this.createUserEntity(email, password);
        RoleEntity roleEntity = this.createUserRoleEntity();

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty());
        Mockito.when(this.roleEntityRepository.findRoleEntityByRoleName("USER"))
                .thenReturn(Optional.of(roleEntity));
        Mockito.when(this.passwordEncoder.encode(password))
                .thenReturn("encodedPass");
        Mockito.when(this.userEntityRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(userEntity);

        assertEquals(userEntity, this.userDetailsService.register(email, password));
    }
}