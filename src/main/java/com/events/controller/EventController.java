package com.events.controller;

import com.events.DTO.EventCreateDto;
import com.events.DTO.EventRegistrationDto;
import com.events.entity.Event;
import com.events.repositories.EventRepository;
import com.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/event")
@CrossOrigin()
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;


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
    public ResponseEntity<?> registerEvent(@PathVariable int id, @RequestBody EventRegistrationDto eventRegistrationDto) {

        eventService.regeventmember(eventRegistrationDto, id);
        return ResponseEntity.ok().build();
    }


    }