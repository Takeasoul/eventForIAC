package com.events.service;


import com.events.utils.mail.AbstractEmailContext;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final DocumentService documentService;

    public void sendSimpleEmail(String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        emailSender.send(simpleMailMessage);
    }


    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        //FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
        //messageHelper.addAttachment("Purchase Order", file);
        emailSender.send(mimeMessage);
    }

    public void sendMailWithPdf(AbstractEmailContext emailContext, Map<String, Object> pdfContext) throws MessagingException, IOException, DocumentException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(emailContext.getContext());
        String emailContent = templateEngine.process(emailContext.getTemplateLocation(), context);

        mimeMessageHelper.setTo(emailContext.getTo());
        mimeMessageHelper.setSubject(emailContext.getSubject());
        //mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);

        ByteArrayInputStream bis = documentService.generatePdfQrReport(pdfContext);
        InputStreamSource inputStreamSource = new ByteArrayResource(bis.readAllBytes());
        mimeMessageHelper.addAttachment("Invitation.pdf",inputStreamSource);
        emailSender.send(message);
    }

    public void sendMail(AbstractEmailContext emailContext) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(emailContext.getContext());
        String emailContent = templateEngine.process(emailContext.getTemplateLocation(), context);

        mimeMessageHelper.setTo(emailContext.getTo());
        mimeMessageHelper.setSubject(emailContext.getSubject());
        //mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);

        emailSender.send(message);
    }
}
