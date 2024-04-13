package com.keshavarzi.airplanestats.security.service;

import com.keshavarzi.airplanestats.model.RoleEntity;
import com.keshavarzi.airplanestats.model.UserEntity;
import com.keshavarzi.airplanestats.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    @Autowired
    public UserDetailsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    /**
     * Will load user by email, and associate a Spring Security role
     * @param email the email identifying the user whose data is required.
     * @return returns a Spring User object
     * @throws UsernameNotFoundException: Email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = this.userEntityRepository
                .findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email: " + email + " was not found."));

        Collection<GrantedAuthority> grantedAuthoritiesToUser;
        try {
            grantedAuthoritiesToUser = mapRolesToAuthorities(userEntity.getRoleEntities());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error adding roles to user: " + e);
        }
        return new User(userEntity.getEmail(), userEntity.getPassword(), grantedAuthoritiesToUser);
    }

    /**
     * Use Spring security roles to associate with a user
     * @param roleEntities: will send in list of roleEntities
     * @return List of Spring Security Roles
     */
    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .map((roleEntity) ->
                        new SimpleGrantedAuthority(roleEntity.getRoleName()))
                .collect(Collectors.toCollection(List::of));
    }

}
