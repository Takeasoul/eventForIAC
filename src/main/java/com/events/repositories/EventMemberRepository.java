package com.events.repositories;

import com.events.entity.EventMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventMemberRepository extends CrudRepository<EventMember, UUID> {
    List<EventMember> findByEventId(UUID eventId);

    List<EventMember> findByEventIdAndApproved(UUID eventId, Boolean approved);
}