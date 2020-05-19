package com.organization.webapp.repository;

import com.organization.webapp.models.ERoles;
import com.organization.webapp.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRole(ERoles name);
}
