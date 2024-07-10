package com.events.service;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.exceptions.AppError;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    public ResponseEntity<?> deleteById(UUID id) {
        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    public Event editEvent(EventCreateDto eventCreateDto, UUID id) {
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

    public ResponseEntity<?> regeventmember(EventRegistrationDto eventRegistrationDto, UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getReg_open()) { // Изменено условие на отрицательное
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Registration for this event is closed"));
        }

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

        eventMemberRepository.save(eventMember);
        return ResponseEntity.ok(new AppError(HttpStatus.OK.value(), "Registration successful"));
    }

    public void approvemember(UUID id) {
        Event_Member eventMember = eventMemberRepository.findById(id).orElse(null);
        assert eventMember != null;
        eventMember.setApproved(true);

    }

    public List<Event_Member> findMembersByEventId(UUID eventId) {
        return eventMemberRepository.findByEventId(eventId);
    }

    public Event_Member findEventMemberById(UUID eventMemberId) {
        return eventMemberRepository.findById(eventMemberId).orElse(null);
    }

    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    public Optional<Event_Member> findMemberById(UUID id) {
        return eventMemberRepository.findById(id);
    }

    public ResponseEntity<?> getAllEvents()
    {

        return ResponseEntity.ok(eventRepository.findAll());
    }

    public ResponseEntity<?> getAllEventsByOrgId(UUID orgId) {
        List<Event> events = eventRepository.findByOrgid(orgId);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No events found for the given orgId.");
        }
        return ResponseEntity.ok(events);
    }

}
