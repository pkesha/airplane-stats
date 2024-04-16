package com.keshavarzi.airplanestats.controller;

import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailExistException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.request.LoginRequest;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.service.UserAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/user/authorization", name = "UserAuthorizationController")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorizationController {

    private UserAuthorizationService userAuthorizationService;

    /**
     * Register a new user with email and password
     * @param registerRequest DTO to create user
     * @return HttpStatus.NOT_ACCEPTABLE: Invalid username or password
     *
     * <p>HttpStatus.CONFLICT: Existing Email in database (plane_stats.user_data.user)</p>
     *
     * <p>HttpStatus.NOT_FOUND: 'USER' Spring security role not present in database (plane_stats.user_data.role).
     *      Roles should exist in database at all times</p>
     *
     * <p>HttpStatus.CREATED: User has been created with encoded password & stored in database (plane_stats.user_data.user)
     *      as a 'USER' Spring security user</p>
     */
    @PostMapping(path = "register", name = "RegisterUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        try {
            this.userAuthorizationService.register(email, registerRequest.getPassword());
            return new ResponseEntity<>("User with email " + email + " has been created", HttpStatus.CREATED);
        } catch (InvalidEmailException | InvalidPasswordException invalidCredentials) {
            return new ResponseEntity<>(invalidCredentials.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (EmailExistException emailExistException) {
            return new ResponseEntity<>(emailExistException.getMessage(), HttpStatus.CONFLICT);
        } catch (AuthorizationRoleMissingException authorizationRoleMissingException) {
            return new ResponseEntity<>(authorizationRoleMissingException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "login", name = "UserLogin",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        try {
            this.userAuthorizationService.login(loginRequest.getEmail(), loginRequest.getPassword());
        } catch (UsernameNotFoundException usernameNotFoundException) {
            return new ResponseEntity<>(usernameNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Successful login", HttpStatus.OK);
    }
}
