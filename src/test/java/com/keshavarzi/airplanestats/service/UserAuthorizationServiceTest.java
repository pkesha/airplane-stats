package com.keshavarzi.airplanestats.service;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = UserAuthorizationService.class)
public class UserAuthorizationServiceTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @MockBean
    UserEntityRepository userEntityRepository;
    @MockBean
    RoleEntityRepository roleEntityRepository;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    PasswordEncoder passwordEncoder;

    UserAuthorizationService userAuthorizationService;

    @BeforeEach
    void setUp() {
        this.userAuthorizationService =
                new UserAuthorizationService(this.userEntityRepository, this.roleEntityRepository,
                        this.authenticationManager, this.passwordEncoder);
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
    void registerWithInvalidEmailAddress() {
        String email = "registerWithInvalidEmailAddress";
        String password = "password";

        assertThrows(InvalidEmailException.class, () ->
                this.userAuthorizationService.register(email, password));

    }

    @Test
    void registerWithInvalidPasswordLessThan8Characters(){
        String email = "registerWithInvalidPasswordLessThan8Characters@test.com";
        String password = "test";

        assertThrows(InvalidPasswordException.class, () ->
                this.userAuthorizationService.register(email, password));
    }

    @Test
    void registerWithInvalidPasswordMoreThan15Characters(){
        String email = "registerWithInvalidPasswordMoreThan15Characters@test.com";
        String password = "registerWithInvalidPasswordMoreThan15Characters";

        assertThrows(InvalidPasswordException.class, () ->
                this.userAuthorizationService.register(email, password));
    }

    @Test
    void registerWithExistingEmail() {
        String email = "validEmailTest@test.com";
        String password = "validPassword";
        UserEntity userEntity = this.createUserEntity(email, password);

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));

        assertThrows(EmailExistException.class, () ->
                this.userAuthorizationService.register(email, password));
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
                this.userAuthorizationService.register(email, password));
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

        assertEquals(userEntity, this.userAuthorizationService.register(email, password));
    }

    @Test
    void loginFailedEmailNotFound() {
        String email = "emailDoesNotExist@test.com";
        String password = "validPass";

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> this.userAuthorizationService.login(email, password));
    }

    @Test
    void loginFailedAuthentication() {
        String email = "loginFailedAuthentication@test.com";
        String password = "invalidPass";

        UserEntity userEntity = this.createUserEntity(email, password);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(this.authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> this.userAuthorizationService.login(email, password));
    }

    @Test
    void successfulLogin() {
        String email = "loginFailedAuthentication@test.com";
        String password = "invalidPass";

        UserEntity userEntity = this.createUserEntity(email, password);
        Authentication authentication = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        Mockito.when(this.userEntityRepository.findUserEntityByEmail(email))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(this.authenticationManager.authenticate(authentication))
                .thenReturn(authentication);
    }
}
