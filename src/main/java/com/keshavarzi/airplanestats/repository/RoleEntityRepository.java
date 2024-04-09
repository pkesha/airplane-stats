package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findRoleEntityByRoleName(String name);
}
