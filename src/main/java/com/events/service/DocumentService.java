package com.events.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentService {
    private final static Path root = Paths.get("src/main/resources/static/qr");


    public static ByteArrayInputStream generatePdf(String qrFilename)
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
                PDFont font = PDType1Font.HELVETICA_BOLD;
                // setting font family and font size
                cs.setFont(font, 14);
                // Text color in PDF
                cs.setNonStrokingColor(Color.BLUE);
                // set offset from where content starts in PDF
                cs.newLineAtOffset(20, 750);
                cs.showText("Hello! This PDF is created using PDFBox");
                cs.newLine();
                cs.endText();
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

    public static ByteArrayInputStream generateWord(String qrFilename)
            throws FileNotFoundException, IOException,
            InvalidFormatException {

        Resource resource;
        try {
            Path file = root.resolve(qrFilename);
            resource = new UrlResource(file.toUri());

            if (!(resource.exists() || resource.isReadable())) {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }

        try (XWPFDocument doc = new XWPFDocument()) {

            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);
            // Set Text to Bold and font size to 22 for first paragraph
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setItalic(true);
            r1.setFontSize(22);
            r1.setText("Spring Boot + Apache POI Example");
            r1.setFontFamily("Courier");
            r1.setColor("008000");
            r1.addBreak();

            XWPFParagraph p2 = doc.createParagraph();
            // Set color for second paragraph
            XWPFRun r2 = p2.createRun();
            r2.setText("Spring Boot + Apache POI Example");
            r2.setColor("FF5733");
            r2.setEmbossed(true);
            r2.setStrikeThrough(true);
            r2.addBreak();
            r2.addBreak();

            XWPFParagraph p3 = doc.createParagraph();
            p3.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r3 = p3.createRun();
            r3.setBold(true);
            r3.setItalic(true);
            r3.setFontSize(22);
            r3.setText("Table");
            r3.setFontFamily("Arial");

            XWPFTable table = doc.createTable();
            // Creating first Row
            XWPFTableRow row1 = table.getRow(0);
            row1.getCell(0).setText("Java, Scala");
            row1.addNewTableCell().setText("PHP, Flask");
            row1.addNewTableCell().setText("Ruby, Rails");

            // Creating second Row
            XWPFTableRow row2 = table.createRow();
            row2.getCell(0).setText("C, C ++");
            row2.getCell(1).setText("Python, Kotlin");
            row2.getCell(2).setText("Android, React");

            // add png image
            XWPFRun r4 = doc.createParagraph().createRun();
            r4.addBreak();
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            try (FileInputStream is = new FileInputStream(resource.getFile())) {
                r.addPicture(is, Document.PICTURE_TYPE_PNG, resource.getFilename(),
                        Units.toEMU(500), Units.toEMU(200));

            } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
                throw new RuntimeException(e);
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            doc.write(b);
            return new ByteArrayInputStream(b.toByteArray());
        }

    }
}
