package com.events.controller;

import com.events.service.DocumentService;
import com.events.service.EmailService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RequestMapping("api/document")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class DocumentController {
    @GetMapping(value = "/word",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> word()
            throws IOException, InvalidFormatException {
        ByteArrayInputStream bis = DocumentService.generateWord("qr.png");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=mydoc.docx");
        return ResponseEntity.ok().headers(headers).
                body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/pdf",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> pdf()
            throws IOException, InvalidFormatException {
        ByteArrayInputStream bis = DocumentService.generatePdf("qr.png");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=mydoc.pdf");
        return ResponseEntity.ok().headers(headers).
                body(new InputStreamResource(bis));
    }
}
