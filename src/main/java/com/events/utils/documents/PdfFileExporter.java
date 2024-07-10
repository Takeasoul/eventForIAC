package com.events.utils.documents;

import com.events.generator.QRCodeGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PdfFileExporter {

    private final SpringTemplateEngine templateEngine;

    private final String RESOURCE_PATH = "src/main/resources";

    public ByteArrayInputStream exportPdfFile(String templateFileName, Map<String, Object> data) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
            String url = "http://localhost:8080/api/document/pdf2?memberId=" + data.get("memberId");
            BufferedImage qrCodeImage = qrCodeGenerator.generateQrCode(url);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            System.out.println(base64Image);
            String image = "data:image/png;base64," + base64Image;
            System.out.println(image);
            data.put("qr_image",image);
//            File outputfile = new File(RESOURCE_PATH + "/static/images/qr.png");
//            ImageIO.write(qrCodeImage, "png", outputfile);

            ITextRenderer renderer = new ITextRenderer();
            URL fontResourceURL1 = getClass().getResource("/static/Manrope.ttf");
            URL fontResourceURL2 = getClass().getResource("/static/Unbounded.ttf");
            //renderer.getFontResolver().addFont(getClass().getClassLoader().getResource("static/Roboto-Black.ttf").toString(), true);
            renderer.getFontResolver().addFont(fontResourceURL1.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.getFontResolver().addFont(fontResourceURL2.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            String htmlContent = generateHtml(templateFileName, data);
            System.out.println(htmlContent);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateHtml(String templateFileName, Map<String, Object> data) {
        //TemplateEngine templateEngine = createTemplateEngine();
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process(templateFileName, context);
        return htmlContent;
    }

//    private TemplateEngine createTemplateEngine() {
//        ClassLoaderTemplateResolver pdfTemplateResolver = new ClassLoaderTemplateResolver();
//        pdfTemplateResolver.setPrefix("templates/");
//        pdfTemplateResolver.setSuffix(".html");
//        pdfTemplateResolver.setTemplateMode("HTML5");
//        pdfTemplateResolver.setCharacterEncoding("UTF-8");
//        pdfTemplateResolver.setOrder(1);
//
//        TemplateEngine templateEngine = new TemplateEngine();
//        templateEngine.setTemplateResolver(pdfTemplateResolver);
//        return templateEngine;
//    }

}
