package com.events.repositories;

import com.events.entity.MembersRoles;
import com.events.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembersRolesRepository extends CrudRepository<MembersRoles, UUID> {
    MembersRoles findByRole(String role);
}