package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.RoleEntity;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface to search plane-stats.user_data.role table.
 */
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findRoleEntityByRoleName(@NonNull final String name);
}
