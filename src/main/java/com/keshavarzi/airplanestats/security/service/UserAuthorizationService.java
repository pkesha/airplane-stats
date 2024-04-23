package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailAlreadyExistsException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.model.response.AuthorizationResponse;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.jwt.JwtGenerator;
import java.util.Collections;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorizationService {

    private static final Pattern VALID_EMAIL_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final AuthenticationManager authenticationManager;
    private UserEntityRepository userEntityRepository;
    private RoleEntityRepository roleEntityRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    /**
     * Registers a new user if criteria are met
     *
     * @param userEmail User's email
     * @param password  User's password
     * @throws InvalidPasswordException          Invalid password length
     * @throws InvalidEmailException             Invalid Email formatting
     * @throws EmailAlreadyExistsException       Email already exists in database (plane_stats.user_data.user)
     * @throws AuthorizationRoleMissingException Authorization role does not exist in database (plane_stats.user_data.role)
     */
    public UserEntity register(@NonNull final String userEmail, @NonNull final String password) throws
            InvalidPasswordException, InvalidEmailException, EmailAlreadyExistsException, AuthorizationRoleMissingException {

        if (!VALID_EMAIL_ADDRESS.matcher(userEmail).matches()) {
            throw new InvalidEmailException(userEmail);
        } else if (!((password.length() >= 8) && (password.length() <= 15))) {
            throw new InvalidPasswordException("Invalid password length");
        } else if (this.userEntityRepository.findUserEntityByEmail(userEmail).isPresent()) {
            throw new EmailAlreadyExistsException(userEmail);
        } else if (this.roleEntityRepository.findRoleEntityByRoleName("USER").isEmpty()) {
            throw new AuthorizationRoleMissingException("USER");
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(userEmail);
            userEntity.setPassword(this.passwordEncoder.encode(password));

            RoleEntity roles = this.roleEntityRepository
                    .findRoleEntityByRoleName("USER")
                    .get();

            userEntity.setRoleEntities(Collections.singletonList(roles));
            return this.userEntityRepository.save(userEntity);
        }
    }

    public AuthorizationResponse login(@NonNull final String email, @NonNull final String password) {
        if (this.userEntityRepository.findUserEntityByEmail(email).isEmpty()) {
            throw new UsernameNotFoundException(email + "not found");
        } else {
            Authentication authentication = this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = this.jwtGenerator.generateToken(authentication);
            AuthorizationResponse authorizationResponse = new AuthorizationResponse();
            authorizationResponse.setAccessToken(token);
            return authorizationResponse;
        }
    }

}
