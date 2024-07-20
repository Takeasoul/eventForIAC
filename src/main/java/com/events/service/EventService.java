package com.events.service;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.entity.EventMember;
import com.events.entity.MembersRoles;
import com.events.exceptions.AppError;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import com.events.repositories.MembersRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final MembersRolesRepository membersRolesRepository;

    public Event createNewEvent(EventCreateDto eventCreateDto) {

        Event event = new Event();
        event.setName(eventCreateDto.getEvent_name());
        event.setSummary(eventCreateDto.getEvent_summary());
        event.setDate(eventCreateDto.getEvent_date());
        event.setOrganizer(eventCreateDto.getOgr_id());
        event.setRegOpen(eventCreateDto.getReg_open());
        event.setAddress(eventCreateDto.getAddress());
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
            event.setName(eventCreateDto.getEvent_name());
            event.setSummary(eventCreateDto.getEvent_summary());
            event.setDate(eventCreateDto.getEvent_date());
            event.setRegOpen(eventCreateDto.getReg_open());
            event = eventRepository.save(event);
        }
        return event;
    }

    public ResponseEntity<?> regeventmember(EventRegistrationDto eventRegistrationDto, UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getRegOpen()) { // Изменено условие на отрицательное
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Registration for this event is closed"));
        }

        EventMember eventMember = new EventMember();
        eventMember.setFirstname(eventRegistrationDto.getFirstname());
        eventMember.setMiddlename(eventRegistrationDto.getMiddlename());
        eventMember.setLastname(eventRegistrationDto.getLastname());
        eventMember.setCompany(eventRegistrationDto.getCompany());
        eventMember.setPosition(eventRegistrationDto.getPosition());
        eventMember.setEmail(eventRegistrationDto.getEmail());
        eventMember.setPhone(eventRegistrationDto.getPhone());
        eventMember.setClubmember(eventRegistrationDto.getClubmember());
        eventMember.setApproved(null);
        eventMember.setEventId(id);
        String role = "Участник";
        System.out.println(role);
        MembersRoles participantRole = membersRolesRepository.findByRole(role);
        System.out.println("Found role: " + participantRole);
        Iterable<MembersRoles> roles = membersRolesRepository.findAll();
        if (participantRole != null) {
            // Установить ID роли в объект eventMember
            eventMember.setEventMembersRoleId(participantRole.getId());

            // Сохранить eventMember в репозитории
            EventMember savedEventMember = eventMemberRepository.save(eventMember);

            // Вернуть успешный ответ с ID сохраненного eventMember
            return ResponseEntity.ok(Collections.singletonMap("id", savedEventMember.getId()));
        } else {
            throw new RuntimeException("Role 'Участник' not found");
        }

    }

    public void approvemember(UUID id) {

       EventMember event_member = eventMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Event member not found"));
       event_member.setApproved(true);
       eventMemberRepository.save(event_member);
    }

    public void unapprovemember(UUID id) {
        System.out.println("sadasdasd");
        EventMember event_member = eventMemberRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        event_member.setApproved(false);
        eventMemberRepository.save(event_member);
    }

    public List<EventMember> findMembersByEventId(UUID eventId) {
        return eventMemberRepository.findByEventId(eventId);
    }

    public List<EventMember> findApprovedMembersByEventId(UUID eventId, Boolean approved) {
        return eventMemberRepository.findByEventIdAndApproved(eventId,approved);
    }

    public EventMember findEventMemberById(UUID eventMemberId) {
        return eventMemberRepository.findById(eventMemberId).orElse(null);
    }

    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    public Optional<EventMember> findMemberById(UUID id) {
        return eventMemberRepository.findById(id);
    }

    public ResponseEntity<?> getAllEvents()
    {

        return ResponseEntity.ok(eventRepository.findAll());
    }

    public ResponseEntity<?> getAllEventsByOrgId(UUID orgId) {
        List<Event> events = eventRepository.findByOrgId(orgId);
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No events found for the given orgId.");
        }
        return ResponseEntity.ok(events);
    }

    public ResponseEntity<?> addEventMemberRole(String rolename) {

        MembersRoles newRole = new MembersRoles();
        newRole.setRole(rolename);
        membersRolesRepository.save(newRole);
        return ResponseEntity.ok().build();
    }

    public String getRoleNameById(UUID id) {
        membersRolesRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return membersRolesRepository.findById(id).get().getRole();
    }


    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(membersRolesRepository.findAll());
    }


}
