package com.keshavarzi.airplanestats.service;

import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailExistException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserAuthorizationService {

    private UserEntityRepository userEntityRepository;
    private RoleEntityRepository roleEntityRepository;
    private PasswordEncoder passwordEncoder;
    private static final Pattern VALID_EMAIL_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Registers a new user if criteria are met
     * @param userEmail User's email
     * @param password User's password
     * @throws InvalidPasswordException Invalid password length
     * @throws InvalidEmailException Invalid Email formatting
     * @throws EmailExistException Email already exists in database (plane_stats.user_data.user)
     * @throws AuthorizationRoleMissingException Authorization role does not exist in database (plane_stats.user_data.role)
     */
    public UserEntity register(String userEmail, String password) throws
            InvalidPasswordException, InvalidEmailException, EmailExistException, AuthorizationRoleMissingException {

        if (!VALID_EMAIL_ADDRESS.matcher(userEmail).matches()) {
            throw new InvalidEmailException("Invalid email address");
        } else if (!((password.length() >= 8) && (password.length() <= 15))) {
            throw new InvalidPasswordException("Invalid password length");
        } else if (this.userEntityRepository.findUserEntityByEmail(userEmail).isPresent()) {
            throw new EmailExistException(userEmail);
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

    public void login(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
