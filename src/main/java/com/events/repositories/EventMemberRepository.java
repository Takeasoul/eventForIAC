package com.events.repositories;

import com.events.entity.Event;
import com.events.entity.Event_Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventMemberRepository extends CrudRepository<Event_Member, UUID> {
    List<Event_Member> findByEventId(UUID eventId);
}