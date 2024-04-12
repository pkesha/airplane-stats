package com.keshavarzi.airplanestats.security;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userEntityRepository
                .findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email: " + username + " was not found."));

        Collection<GrantedAuthority> grantedAuthoritiesToUser;
        try {
            grantedAuthoritiesToUser = mapRolesToAuthorities(userEntity.getRoleEntities());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error adding roles to user: " + e);
        }
        return new User(userEntity.getEmail(), userEntity.getPassword(), grantedAuthoritiesToUser);
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .map((roleEntity) -> new SimpleGrantedAuthority(
                        roleEntity.getRoleName()))
                .collect(Collectors.toCollection(List::of));
    }

}
