package com.events.service.Schedule;

import com.events.entity.Event;
import com.events.repositories.EventRepository;
import com.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EventScheduleService {

    private final EventRepository eventRepository;
    @Autowired
    public EventScheduleService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${interval-delete-event-cron}")
    public void changeRegistrationStatus(){
        LocalDate currentDate = LocalDate.now();

        List<Event> events = StreamSupport
                .stream(eventRepository.findAll().spliterator(), false)
                .toList();

        List<Event> updatedEvents = events.stream()
                .peek(event -> {
                    LocalDate eventDate = event.getEvent_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();//добавить вторую дату и изменить на даты открытия и закрытия
                    if (event.getReg_open() && currentDate.isAfter(eventDate)) {
                        event.setReg_open(false);
                    } else if (!event.getReg_open() && currentDate.isBefore(eventDate)) {
                        event.setReg_open(true);
                    }
                })
                .toList();
        if(!updatedEvents.isEmpty())
            eventRepository.saveAll(updatedEvents);

        System.out.println(updatedEvents);
    }

}
