package com.events.controller;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import com.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/event")
@CrossOrigin()
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;


    @PostMapping("/createEvent")
    public Event createNewEvent(@RequestBody EventCreateDto eventCreateDto) {
        return eventService.createNewEvent(eventCreateDto);
    }

    @GetMapping("/eventsAll")
    public ResponseEntity<?> getAll()
    {
        return eventService.getAllEvents();
    }

    @PostMapping("/deleteEvent/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id){
        return eventService.deleteById(id);
    }

    @PostMapping("/editEvent/{id}")
    public ResponseEntity<Event> editById(@PathVariable UUID id, @RequestBody EventCreateDto eventCreateDto) {
        Event updatedEvent = eventService.editEvent(eventCreateDto, id);
        if (updatedEvent != null) {
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerEvent(@PathVariable UUID id, @RequestBody EventRegistrationDto eventRegistrationDto) {
        return eventService.regeventmember(eventRegistrationDto, id);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable UUID id) {

        eventService.approvemember(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMembers(@PathVariable UUID id) {
        List<Event_Member> members = eventService.findMembersByEventId(id);
        if (members.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<?> getEventInfo(@PathVariable UUID id) {
        Optional<Event> event = eventService.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(event);
    }


    @GetMapping("/{id}/members/{memberid}")
    public ResponseEntity<?> getCurrentMembers(@PathVariable UUID id, @PathVariable UUID memberid) {
        Optional<Event> member = eventService.findById(memberid);
        if (member.isEmpty() && Objects.equals(memberid, id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(member);
    }

    @GetMapping("/eventsAll/{id}")
    public ResponseEntity<?> getAll(@PathVariable UUID id)
    {
        return eventService.getAllEventsByOrgId(id);
    }

    // Добавляем новый метод с уникальным URL
    @GetMapping("/eventsAllByOrgId")
    public ResponseEntity<?> getAllByOrgId(@RequestParam UUID orgId) {
        return eventService.getAllEventsByOrgId(orgId);
    }

    @GetMapping("/memberInfo/{id}")
    public ResponseEntity<?> getMemberInfo(@PathVariable UUID id)
    {
        System.out.println("Зашел" + id);
        Optional<Event_Member> member = eventService.findMemberById(id);
        if(member.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(member);
    }
}