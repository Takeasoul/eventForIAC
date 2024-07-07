package com.events.controller;

import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.repositories.EventMemberRepository;
import com.events.repositories.EventRepository;
import com.events.service.DocumentService;
import com.events.service.EmailService;
import com.events.service.EventService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@RequestMapping("api/document")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class DocumentController {
    private final DocumentService documentService;
    @Autowired
    EventService eventService;
    @Autowired
    EventMemberRepository eventMemberRepository;

    @GetMapping(value = "/word",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> word()
            throws IOException, InvalidFormatException {
        ByteArrayInputStream bis = documentService.generateWordBadge(UUID.fromString("6c81ae12-7bac-4eab-ae1a-160743edbdaf"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=mydoc.docx");
        return ResponseEntity.ok().headers(headers).
                body(new InputStreamResource(bis));
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

    @GetMapping("/pdf2")
    public ResponseEntity<InputStreamResource> generatePdf(@RequestParam UUID memberId)
            throws IOException, InvalidFormatException {
        Event_Member eventMember = eventMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Event Member not found"));

        ByteArrayInputStream bis = documentService.generatePdf("qr.png", eventMember, eventService.findById(eventMember.getEventId()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=mydoc.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }
}
