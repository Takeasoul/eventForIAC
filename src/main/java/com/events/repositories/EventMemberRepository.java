package com.events.repositories;

import com.events.entity.Event;
import com.events.entity.Event_Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMemberRepository extends CrudRepository<Event_Member, Long> {
    List<Event_Member> findByEventId(Long eventId);
}