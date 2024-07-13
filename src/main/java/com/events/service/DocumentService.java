package com.events.service;


import com.deepoove.poi.XWPFTemplate;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.generator.QRCodeGenerator;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EventService eventService;



    private final String BADGE_TEMPLATE_PATH = "src/main/resources/static/badge_template.docx";


    private final SpringTemplateEngine templateEngine;

    public  ByteArrayInputStream generatePdfQrReport(Map<String, Object> context)
            throws DocumentException, IOException {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
            String url = "http://localhost:5173/event-member-info/" + context.get("memberId");
            BufferedImage qrCodeImage = qrCodeGenerator.generateQrCode(url);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            String image = "data:image/png;base64," + base64Image;
            context.put("qr_image",image);

            ITextRenderer renderer = new ITextRenderer();
            Resource fontResource1 = new ClassPathResource("/static/Manrope.ttf");
            InputStream inputStream1 = fontResource1.getInputStream();

            Resource fontResource2 = new ClassPathResource("/static/Unbounded.ttf");
            InputStream inputStream2 = fontResource2.getInputStream();

            File tempDir = Files.createTempDirectory("fonts").toFile();

            // Создаем временные файлы для копирования
            File tempFile1 = new File(tempDir, "Manrope.ttf");
            File tempFile2 = new File(tempDir, "Unbounded.ttf");

        // Копируем данные из InputStream во временные файлы
            Files.copy(inputStream1, tempFile1.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(inputStream2, tempFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);

            renderer.getFontResolver().addFont(tempFile1.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.getFontResolver().addFont(tempFile2.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            String htmlContent = generateHtml("qr_pdf.html", context);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            return new ByteArrayInputStream(outputStream.toByteArray());

    }

    public  ByteArrayInputStream generatePdfBadges(List<Event_Member> members, String eventName)
            throws DocumentException, IOException {

        List<ArrayList<Event_Member>> members_pairs = new ArrayList<>();
        for(int i = 0; i < members.size(); i += 2){
            ArrayList<Event_Member> pair = new ArrayList<>();
            pair.add(members.get(i));
            if (i+1 < members.size()){
                pair.add(members.get(i+1));
            }
            members_pairs.add(pair);
        }



        Map<String, Object> context = new HashMap<>();
        context.put("members_pairs",members_pairs);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        context.put("event_name", eventName);

        ITextRenderer renderer = new ITextRenderer();

        File tempFontFile1 = createTempFontFile("/static/Manrope.ttf");
        File tempFontFile2 = createTempFontFile("/static/Unbounded.ttf");
        renderer.getFontResolver().addFont(tempFontFile1.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont(tempFontFile2.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        String htmlContent = generateHtml("badge.html", context);
        System.out.println(htmlContent);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream, false);
        renderer.finishPDF();
        return new ByteArrayInputStream(outputStream.toByteArray());

    }

    public  ByteArrayInputStream generatePdf(String qrFilename, Event_Member eventMember, Optional<Event> event) {
        try {
            PDDocument pdDoc = new PDDocument();
            PDPage page = new PDPage();
            // add page to the document
            pdDoc.addPage(page);
            // write to a page content stream
            try(PDPageContentStream cs = new PDPageContentStream(pdDoc, page)){
                cs.beginText();
                Resource resource = new ClassPathResource("/static/arialmt.ttf");
                PDType0Font font = PDType0Font.load(pdDoc, resource.getInputStream());
                // setting font family and font size
                cs.setFont(font, 14);
                // Text color in PDF
                cs.setNonStrokingColor(Color.BLUE);
                // set offset from where content starts in PDF
                cs.newLineAtOffset(20, 750);
                String eventName;
                if (event.isPresent()) {
                    eventName = event.get().getEvent_name();
                }
                else  eventName = "ОШИБКА";

                String text = String.format("Hello %s %s %s! You are invited to the event: %s",
                        eventMember.getFirstname(), eventMember.getMiddlename(), eventMember.getLastname(),
                        eventName);
                cs.showText(text);
                cs.endText();
                QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
                BufferedImage qrCodeImage = qrCodeGenerator.generateQrCode("https://www.youtube.com/watch?v=QGx0pP3Uk0c&ab_channel=%D0%90%D1%81%D0%B0%D1%84%D1%8C%D0%B5%D0%B2.%D0%96%D0%B8%D0%B7%D0%BD%D1%8C");
                PDImageXObject pdImage = LosslessFactory.createFromImage(pdDoc, qrCodeImage);
                cs.drawImage(pdImage, 200, -10);
            }
            // save and close PDF document
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            pdDoc.save(b);
            pdDoc.close();
            return new ByteArrayInputStream(b.toByteArray());
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ByteArrayInputStream generateWordBadge(UUID eventMemberId)
            throws FileNotFoundException, IOException,
            InvalidFormatException {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();

            Event_Member eventMember = eventService.findEventMemberById(eventMemberId);
            if (eventMember == null){
                return null;
            }
            Event event = eventService.findById(eventMember.getEventId()).orElseThrow(
                    (()-> new RuntimeException(MessageFormat.format("Event with id {0} not found!",eventMember.getEventId())))
            );

            XWPFTemplate.compile(BADGE_TEMPLATE_PATH).render(new HashMap<String, Object>(){{
                put("first_name", eventMember.getFirstname());
                put("last_name", eventMember.getLastname());
                put("role", "Участник");
                put("event", event.getEvent_name());
            }}).writeAndClose(bis);

            return new ByteArrayInputStream(bis.toByteArray());


    }
    public ByteArrayInputStream generateListOfWordBadges(UUID eventId)
            throws FileNotFoundException, IOException,
            InvalidFormatException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream();

        List<Event_Member> members = new ArrayList<>();

        members = eventService.findMembersByEventId(eventId);

        if (members == null){
            return null;
        }





//        XWPFTemplate.compile("/home/vladimir/IdeaProjects/eventForIAC/src/main/resources/static/badge_template.docx").render(new HashMap<String, Object>(){{
//            put("first_name", eventMember.getFirstname());
//            put("last_name", eventMember.getLastname());
//            put("role", "Участник");
//            put("event", event.getEvent_name());
//        }}).writeAndClose(bis);

        return new ByteArrayInputStream(bis.toByteArray());


    }

    private String generateHtml(String templateFileName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateFileName, context);
    }

    private File createTempFontFile(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        InputStream inputStream = resource.getInputStream();
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".ttf");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }


}
