package com.events.repositories;

import com.events.entity.Members_Roles;
import com.events.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembersRolesRepository extends CrudRepository<Members_Roles, UUID> {
    Members_Roles findByRole(String role);
}