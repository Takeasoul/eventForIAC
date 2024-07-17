package com.events.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final String templatesPath = "templates/"; // Путь к директории с шаблонами

    @GetMapping
    public ResponseEntity<List<String>> getAllTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource(templatesPath);
            Path path = Paths.get(resource.getURI());
            Files.list(path).filter(Files::isRegularFile).forEach(file -> {
                templateNames.add(file.getFileName().toString());
            });
            return ResponseEntity.ok(templateNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{templateName}")
    public ResponseEntity<String> getTemplate(@PathVariable String templateName) {
        try {
            Resource resource = new ClassPathResource(templatesPath + templateName);
            byte[] data = Files.readAllBytes(resource.getFile().toPath());
            String htmlContent = new String(data);
            return ResponseEntity.ok(htmlContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save/{templateId}")
    public ResponseEntity<Void> saveTemplate(@PathVariable String templateId, @RequestBody String htmlContent) {
        try {
            Path path = Paths.get(templatesPath + templateId);
            Files.write(path, htmlContent.getBytes());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{templateId}")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String templateId) {
        try {
            Resource resource = new ClassPathResource(templatesPath + templateId);
            byte[] data = Files.readAllBytes(resource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", templateId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
