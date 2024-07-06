package com.events.repositories;

import com.events.entity.Event;
import com.events.entity.Event_Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMemberRepository extends CrudRepository<Event_Member, Long> {


}