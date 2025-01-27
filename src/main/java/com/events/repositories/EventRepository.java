package com.events.repositories;

import com.events.entity.Event;
import com.events.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, UUID> {


    List<Event> findByOrgId(UUID id);

}