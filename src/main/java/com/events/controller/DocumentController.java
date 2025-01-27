package com.events.controller;

import com.events.entity.Event;
import com.events.entity.EventMember;

import com.events.service.DocumentService;

import com.events.service.EventService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("api/document")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class DocumentController {
    private final DocumentService documentService;
    @Autowired
    EventService eventService;

    @GetMapping(value = "/word/{member-id}",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> word(@PathVariable("member-id") UUID memberId)
            throws IOException, InvalidFormatException {

        ByteArrayInputStream bis = documentService.generateWordBadge(memberId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=badge.docx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }
    /*@GetMapping(value = "/pdf",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> pdf()
            throws IOException, InvalidFormatException {
        ByteArrayInputStream bis = DocumentService.generatePdf("qr.png", eventMember);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=mydoc.pdf");
        return ResponseEntity.ok().headers(headers).
                body(new InputStreamResource(bis));
    }
*/

    @GetMapping("/pdf/qr")
    public ResponseEntity<InputStreamResource> generatePdf(@RequestParam UUID memberId)
            throws IOException, DocumentException {
        EventMember eventMember = eventService.findMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Event member not found"));
        Event event = eventService.findById(eventMember.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Map<String, Object> data = new HashMap<>();
        data.put("event_name", event.getName());
        data.put("first_name", eventMember.getFirstname());
        data.put("middlename", eventMember.getMiddlename());
        data.put("last_name", eventMember.getLastname());
        data.put("email", eventMember.getEmail());
        data.put("phone", eventMember.getPhone());
        data.put("position",eventMember.getPosition());
        data.put("company", eventMember.getCompany());
        data.put("event_date",event.getDate());
        data.put("status", "Участник");
        data.put("memberId",memberId);
        //ByteArrayInputStream bis = documentService.generatePdf("qr.png", eventMember, eventService.findById(eventMember.getEventId()));
        ByteArrayInputStream bis = documentService.generatePdfQrReport(data);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=mydoc.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/pdf/badges")
    public ResponseEntity<?> generatePdfBadges(@RequestParam UUID eventId)
            throws IOException, DocumentException {
        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        List<EventMember> members = eventService.findApprovedMembersByEventId(eventId,true);
        if (members.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Участники мероприятия не найдены");
        }

        ByteArrayInputStream bis = documentService.generatePdfBadges(members,event.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=badges.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }

}
