package com.keshavarzi.airplanestats.controller;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.model.request.RegisterRequest;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "api/authorization", name = "AuthorizationController")
@SuppressFBWarnings("EI_EXPOSE_REP2")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationController {
    private UserEntityRepository userEntityRepository;
    private RoleEntityRepository roleEntityRepository;
    private PasswordEncoder passwordEncoder;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @PostMapping(path = "register", name = "RegisterUser", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String userEmail = registerRequest.getEmail();
        if(userEntityRepository.findUserEntityByEmail(userEmail).isPresent()
                && roleEntityRepository.findRoleEntityByRoleName("USER").isPresent()) {
            return new ResponseEntity<>("Email " + userEmail + " is present", HttpStatus.BAD_REQUEST);
        } else if(!VALID_EMAIL_ADDRESS_REGEX.matcher(userEmail).matches()) {
            return new ResponseEntity<>("Email " + userEmail + " is not a properly formatted email", HttpStatus.BAD_REQUEST);
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(userEmail);
            userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            RoleEntity roles = roleEntityRepository.findRoleEntityByRoleName("USER").get();
            userEntity.setRoleEntities(Collections.singletonList(roles));
            userEntityRepository.save(userEntity);

            return new ResponseEntity<>("User with email " + userEmail + " has been created", HttpStatus.OK);
        }
    }
}
