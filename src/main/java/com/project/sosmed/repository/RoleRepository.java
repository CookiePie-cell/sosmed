package com.project.sosmed.repository;

import com.project.sosmed.entity.Role;
import com.project.sosmed.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
