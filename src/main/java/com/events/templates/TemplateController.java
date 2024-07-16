package com.events.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
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

    private final ResourceLoader resourceLoader;

    @Autowired
    public TemplateController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        try {
            // Получаем все ресурсы из папки resources/templates
            Resource resource = resourceLoader.getResource("classpath:/templates/");
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
    public ResponseEntity<byte[]> getTemplate(@PathVariable String templateName) {
        try {
            // Загружаем указанный шаблон из папки resources/templates
            Resource resource = resourceLoader.getResource("classpath:/templates/" + templateName);
            byte[] data = StreamUtils.copyToByteArray(resource.getInputStream());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", templateName);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save/{templateName}")
    public ResponseEntity<Void> saveTemplate(@PathVariable String templateName, @RequestBody String htmlContent) {
        // Логика для сохранения измененного шаблона
        // Для примера будем выводить в консоль
        System.out.println("Сохранение шаблона: " + templateName);
        System.out.println("Содержимое: " + htmlContent);
        // Реализуйте логику для сохранения содержимого
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{templateName}")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String templateName) {
        try {
            // Загружаем указанный шаблон из папки resources/templates
            Resource resource = resourceLoader.getResource("classpath:/templates/" + templateName);
            byte[] data = StreamUtils.copyToByteArray(resource.getInputStream());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", templateName);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
