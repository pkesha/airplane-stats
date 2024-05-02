package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import com.keshavarzi.airplanestats.security.exception.register.AuthorizationRoleMissingException;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** {@code UserDetailService} custom implementation. */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserEntityRepository userEntityRepository;

  /**
   * Will load user by username, and associate a Spring Security role.
   *
   * @param username Identifies the user in the database
   * @return UserDetails
   * @throws UsernameNotFoundException username is not found
   */
  @Override
  public UserDetails loadUserByUsername(@NonNull final String username) {
    UserEntity userEntity =
        this.userEntityRepository
            .findUserEntityByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    Collection<GrantedAuthority> grantedAuthoritiesToUser =
        userEntity
            .getRoleEntities()
            .orElseThrow(
                () -> new AuthorizationRoleMissingException("Authorization roles not found"))
            .stream()
            .map((roleEntity) -> new SimpleGrantedAuthority(roleEntity.getRoleName()))
            .collect(Collectors.toList());

    return new User(userEntity.getUsername(), userEntity.getPassword(), grantedAuthoritiesToUser);
  }
}
