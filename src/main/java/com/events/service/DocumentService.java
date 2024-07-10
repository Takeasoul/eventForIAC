package com.events.service;

import ch.qos.logback.core.util.Loader;
import com.deepoove.poi.XWPFTemplate;
import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.generator.QRCodeGenerator;
import com.events.utils.documents.PdfFileExporter;
import com.events.utils.mail.AbstractEmailContext;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTBordersImpl;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EventService eventService;

    private final PdfFileExporter pdfFileExporter;

    private final String BADGE_TEMPLATE_PATH = "src/main/resources/static/badge_template.docx";



    public  ByteArrayInputStream generatePdfMessage(Map<String, Object> context){
            ByteArrayInputStream bis = pdfFileExporter.exportPdfFile("qr_pdf.html", context);
            return bis;
    }
    public  ByteArrayInputStream generatePdf(String qrFilename, Event_Member eventMember, Optional<Event> event)
            throws FileNotFoundException, IOException,
            InvalidFormatException {
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
}
