package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
  public UserDetails loadUserByUsername(@NonNull final String username)
      throws UsernameNotFoundException {
    UserEntity userEntity =
        this.userEntityRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Username was not found: " + username));

    Collection<GrantedAuthority> grantedAuthoritiesToUser;
    try {
      grantedAuthoritiesToUser = this.mapRolesToAuthorities(userEntity.getRoleEntities());
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Error adding roles to user: " + e);
    }
    return new User(userEntity.getUsername(), userEntity.getPassword(), grantedAuthoritiesToUser);
  }

  /**
   * Use Spring security roles to associate with a user.
   *
   * @param roleEntities will send in list of roleEntities
   * @return List of Spring Security Roles
   */
  protected Collection<GrantedAuthority> mapRolesToAuthorities(
      final Collection<RoleEntity> roleEntities) {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (RoleEntity roleEntity : roleEntities) {
      grantedAuthorities.add(new SimpleGrantedAuthority(roleEntity.getRoleName()));
    }
    return grantedAuthorities;
  }
}
