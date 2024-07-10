package com.events.controller;


import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.generator.QRCodeGenerator;
import com.events.service.DocumentService;
import com.events.service.EmailService;
import com.events.service.EventService;
import com.events.utils.mail.AbstractEmailContext;
import com.events.utils.mail.EmailContext;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/email")
@CrossOrigin()
@RequiredArgsConstructor
public class EmailController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);


    private final EmailService emailService;

    private final EventService eventService;

    private final DocumentService documentService;
    @GetMapping(value = "/simple-email/{user-email}")
    public @ResponseBody ResponseEntity sendSimpleEmail(@PathVariable("user-email") String email) {
        try {
            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            LOG.error("Error while sending out email..{}", mailException.fillInStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }
    @GetMapping(value = "/{memberId}")
    public @ResponseBody ResponseEntity sendEmail(@PathVariable UUID memberId) throws IOException {
        Event_Member eventMember = eventService.findMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Event member not found"));
        Event event = eventService.findById(eventMember.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Map<String, Object> data = new HashMap<>();
        data.put("event_name", event.getEvent_name());
        data.put("first_name", eventMember.getFirstname());
        data.put("middlename", eventMember.getMiddlename());
        data.put("last_name", eventMember.getLastname());
        data.put("email", eventMember.getEmail());
        data.put("phone", eventMember.getPhone());
        data.put("position",eventMember.getPosition());
        data.put("company", eventMember.getCompany());
        data.put("event_date",event.getEvent_date());
        data.put("status", "Участник");
        data.put("memberId",memberId);
        //ByteArrayInputStream bis = documentService.generatePdf("qr.png", eventMember, eventService.findById(eventMember.getEventId()));
        ByteArrayInputStream bis = documentService.generatePdfMessage(data);
        AbstractEmailContext context = new EmailContext();
        context.setTo(eventMember.getEmail());
        context.setSubject("Приглашение на меропритие");
        context.setTemplateLocation("email_message.html");
        try {
            emailService.sendMail(context,data);
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            LOG.error("Error while sending out email..{}", mailException.fillInStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            LOG.error("Error while sending out email..{}", e.getStackTrace());
            LOG.error("Error while sending out email..{}", e.fillInStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }
    @GetMapping(value = "/simple-order-email/{user-email}")
    public @ResponseBody ResponseEntity sendEmailAttachment(@PathVariable("user-email") String email) {

        try {
            emailService.sendEmailWithAttachment(email, "Order Confirmation", "Thanks for your recent order",
                    "classpath:purchase_order.pdf");
        } catch (MessagingException | FileNotFoundException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox for order confirmation", HttpStatus.OK);
    }

}