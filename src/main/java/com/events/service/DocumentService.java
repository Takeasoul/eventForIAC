package com.events.service;


import com.deepoove.poi.XWPFTemplate;
import com.events.entity.Event;
import com.events.entity.EventMember;
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

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.URL;

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
            URL url1 = new URL("classpath:/static/Manrope.ttf");
            URL url2 = new URL("classpath:/static/Unbounded.ttf");
            String path1 = url1.getPath();
            String path2 = url2.getPath();
            renderer.getFontResolver().addFont(path1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.getFontResolver().addFont(path2, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            String htmlContent = generateHtml("qr_pdf.html", context);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            return new ByteArrayInputStream(outputStream.toByteArray());

    }

    public  ByteArrayInputStream generatePdfBadges(List<EventMember> members, String eventName)
            throws DocumentException, IOException {

        List<ArrayList<EventMember>> members_pairs = new ArrayList<>();
        for(int i = 0; i < members.size(); i += 2){
            ArrayList<EventMember> pair = new ArrayList<>();
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

            URL url1 = new URL("classpath:/static/Manrope.ttf");
            URL url2 = new URL("classpath:/static/Unbounded.ttf");
        String path1 = url1.getPath();
        String path2 = url2.getPath();
        renderer.getFontResolver().addFont(path1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont(path2, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        String htmlContent = generateHtml("badge.html", context);
        System.out.println(htmlContent);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream, false);
        renderer.finishPDF();
        return new ByteArrayInputStream(outputStream.toByteArray());

    }

    public  ByteArrayInputStream generatePdf(String qrFilename, EventMember eventMember, Optional<Event> event) {
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
                    eventName = event.get().getName();
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

            EventMember eventMember = eventService.findEventMemberById(eventMemberId);
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
                put("event", event.getName());
            }}).writeAndClose(bis);

            return new ByteArrayInputStream(bis.toByteArray());


    }
    public ByteArrayInputStream generateListOfWordBadges(UUID eventId)
            throws FileNotFoundException, IOException,
            InvalidFormatException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream();

        List<EventMember> members = new ArrayList<>();

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
}
