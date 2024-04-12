package com.keshavarzi.airplanestats.controller;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api/authorization", name = "AuthorizationController")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationController {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private UserEntityRepository userEntityRepository;
    private RoleEntityRepository roleEntityRepository;
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "register", name = "RegisterUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String userEmail = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(userEmail).matches()) {
            return new ResponseEntity<>("Email " + userEmail + " is not a properly formatted email.", HttpStatus.NOT_ACCEPTABLE);
        } else if (password.length() < 6) {
            return new ResponseEntity<>("Password must be greater than 6 characters.", HttpStatus.NOT_ACCEPTABLE);
        } else if (this.userEntityRepository.findUserEntityByEmail(userEmail).isPresent()) {
            return new ResponseEntity<>("Email " + userEmail + " is present.", HttpStatus.CONFLICT);
        } else if (this.roleEntityRepository.findRoleEntityByRoleName("USER").isEmpty()) {
            return new ResponseEntity<>("No USER roles detected.", HttpStatus.NOT_FOUND);
        }
        else {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(userEmail);
            userEntity.setPassword(this.passwordEncoder.encode(registerRequest.getPassword()));

            RoleEntity roles = this.roleEntityRepository
                    .findRoleEntityByRoleName("USER")
                    .get();
            userEntity.setRoleEntities(Collections.singletonList(roles));
            this.userEntityRepository.save(userEntity);

            return new ResponseEntity<>("User with email " + userEmail + " has been created", HttpStatus.CREATED);
        }
    }
}
