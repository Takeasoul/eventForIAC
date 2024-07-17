package com.events.service.Schedule;

import com.events.entity.Event;
import com.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventScheduleService {

    private final EventService eventService;

    @Autowired
    public EventScheduleService(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(cron = "${interval-delete-event-cron}")
    public void closeRegistry(){

        System.out.println("EVENT_SCHEDULE_SERVICE");
        eventService.closeRegistry();
    }
}
