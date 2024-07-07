package com.events.controller;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import com.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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

    @PostMapping("/deleteEvent/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){
        return eventService.deleteById(id);
    }

    @PostMapping("/editEvent/{id}")
    public ResponseEntity<Event> editById(@PathVariable Long id, @RequestBody EventCreateDto eventCreateDto) {
        Event updatedEvent = eventService.editEvent(eventCreateDto, id);
        if (updatedEvent != null) {
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerEvent(@PathVariable Long id, @RequestBody EventRegistrationDto eventRegistrationDto) {
        return  eventService.regeventmember(eventRegistrationDto, id);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id) {

        eventService.approvemember(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long id) {
        List<Event_Member> members = eventService.findMembersByEventId(id);
        if (members.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<?> getEventInfo(@PathVariable Long id) {
        Optional<Event> event = eventService.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(event);
    }



}