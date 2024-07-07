package com.events.service;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;

    public Event createNewEvent(EventCreateDto eventCreateDto) {

        Event event = new Event();
        event.setEvent_name(eventCreateDto.getEvent_name());
        event.setEvent_summary(eventCreateDto.getEvent_summary());
        event.setEvent_date(eventCreateDto.getEvent_date());
        event.setReg_open(eventCreateDto.getReg_open());
        eventRepository.save(event);
        return event;
    }

    public ResponseEntity<?> deleteById(Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    public Event editEvent(EventCreateDto eventCreateDto, Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null) {
            event.setEvent_name(eventCreateDto.getEvent_name());
            event.setEvent_summary(eventCreateDto.getEvent_summary());
            event.setEvent_date(eventCreateDto.getEvent_date());
            event.setReg_open(eventCreateDto.getReg_open());
            event = eventRepository.save(event);
        }
        return event;
    }

    public void regeventmember(EventRegistrationDto eventRegistrationDto, int id) {

        Event_Member eventMember = new Event_Member();
        eventMember.setFirstname(eventRegistrationDto.getFirstname());
        eventMember.setMiddlename(eventRegistrationDto.getMiddlename());
        eventMember.setLastname(eventRegistrationDto.getLastname());
        eventMember.setCompany(eventRegistrationDto.getCompany());
        eventMember.setPosition(eventRegistrationDto.getPosition());
        eventMember.setEmail(eventRegistrationDto.getEmail());
        eventMember.setPhone(eventRegistrationDto.getPhone());
        eventMember.setClubmember(eventRegistrationDto.getClubmember());
        eventMember.setApproved(false);
        eventMember.setEventId(id);

        eventMember = eventMemberRepository.save(eventMember);
    }

    public void approvemember(Long id) {
        Event_Member eventMember = eventMemberRepository.findById(id).orElse(null);
        assert eventMember != null;
        eventMember.setApproved(true);

    }

    public List<Event_Member> findMembersByEventId(Long eventId) {
        return eventMemberRepository.findByEventId(eventId);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }
}
