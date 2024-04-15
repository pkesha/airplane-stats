package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.exception.register.AuthorizationRoleMissingException;
import com.keshavarzi.airplanestats.exception.register.EmailExistException;
import com.keshavarzi.airplanestats.exception.register.InvalidEmailException;
import com.keshavarzi.airplanestats.exception.register.InvalidPasswordException;
import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.RoleEntityRepository;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleEntityRepository roleEntityRepository;
    private static final Pattern VALID_EMAIL_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Will load user by email, and associate a Spring Security role
     * @param email Identifies the user in the database
     * @return UserDetails
     * @throws UsernameNotFoundException Email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = this.userEntityRepository
                .findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email: " + email + " was not found."));

        Collection<GrantedAuthority> grantedAuthoritiesToUser;
        try {
            grantedAuthoritiesToUser = this.mapRolesToAuthorities(userEntity.getRoleEntities());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error adding roles to user: " + e);
        }
        return new User(userEntity.getEmail(), userEntity.getPassword(), grantedAuthoritiesToUser);
    }

    /**
     * Use Spring security roles to associate with a user
     * @param roleEntities will send in list of roleEntities
     * @return List of Spring Security Roles
     */
    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .map((roleEntity) ->
                        new SimpleGrantedAuthority(roleEntity.getRoleName()))
                .collect(Collectors.toCollection(List::of));
    }

    /**
     * Registers a new user if criteria are met
     * @param userEmail User's email
     * @param password User's password
     * @throws InvalidPasswordException Invalid password length
     * @throws InvalidEmailException Invalid Email formatting
     * @throws EmailExistException Email already exists in database (plane_stats.user_data.user)
     * @throws AuthorizationRoleMissingException Authorization role does not exist in database (plane_stats.user_data.role)
     */
    public UserEntity register (String userEmail, String password) throws
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

}

