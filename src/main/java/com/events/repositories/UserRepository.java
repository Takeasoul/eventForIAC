package com.events.repositories;

import com.events.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findUserByLogin(String username);

    List<User> findAll();

    Optional<User> findUserById(UUID id);
}
