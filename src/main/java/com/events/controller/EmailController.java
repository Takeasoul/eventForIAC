package com.events.controller;


import com.events.entity.Event;
import com.events.entity.Event_Member;
import com.events.service.DocumentService;
import com.events.service.EmailService;
import com.events.service.EventService;
import com.events.utils.mail.AbstractEmailContext;
import com.events.utils.mail.EmailContext;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping(value = "/{memberId}")
    public @ResponseBody ResponseEntity sendEmail(@PathVariable UUID memberId) throws IOException {
        Event_Member eventMember = eventService.findMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Event member not found"));
        Event event = eventService.findById(eventMember.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Map<String, Object> data = new HashMap<>();
        data.put("event_name", event.getEvent_name());
        data.put("event_id", event.getEvent_id());
        data.put("first_name", eventMember.getFirstname());
        data.put("middlename", eventMember.getMiddlename());
        data.put("last_name", eventMember.getLastname());
        data.put("email", eventMember.getEmail());
        data.put("phone", eventMember.getPhone());
        data.put("position",eventMember.getPosition());
        data.put("company", eventMember.getCompany());
        data.put("event_date",event.getEvent_date());
        data.put("status", eventService.getRoleNameById(eventMember.getEventMembersRoleId()));
        data.put("memberId",memberId);
        //ByteArrayInputStream bis = documentService.generatePdf("qr.png", eventMember, eventService.findById(eventMember.getEventId()));
        Map<String, Object> templateContext = new HashMap<>();
        // Вся инфа об участнике поступает в template
        templateContext.put("first_name", eventMember.getFirstname());
        templateContext.put("middlename", eventMember.getMiddlename());
        templateContext.put("last_name", eventMember.getLastname());
        templateContext.put("email", eventMember.getEmail());
        templateContext.put("phone", eventMember.getPhone());
        templateContext.put("position",eventMember.getPosition());
        templateContext.put("company", eventMember.getCompany());
        templateContext.put("member_role", eventService.getRoleNameById(eventMember.getEventMembersRoleId()));
        // Вся инфа о мероприятии, которая поступает в template

        //templateContext.put("event_date",event.getEvent_date());

        templateContext.put("event_name", event.getEvent_name());
        templateContext.put("event_summary", event.getEvent_summary());
        templateContext.put("event_adress", event.getAddress());
        AbstractEmailContext context = new EmailContext();
        context.setContext(templateContext);
        context.setTo(eventMember.getEmail());
        context.setSubject("Приглашение на меропритие: "+ event.getEvent_name());
        context.setTemplateLocation("templates/email_message.html");
        try {
            emailService.sendMailWithPdf(context,data);
        } catch (MailException | MessagingException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            LOG.error("Error while sending out email..{}", mailException.fillInStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DocumentException e) {
            LOG.error("Error while creating document..{}", e.getStackTrace());
            LOG.error("Error while creating document..{}", e.fillInStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }

    @GetMapping(value = "/greetings/{memberId}")
    public @ResponseBody ResponseEntity sendGreetingsEmail(@PathVariable UUID memberId) {
        Event_Member eventMember = eventService.findMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Event member not found"));
        Event event = eventService.findById(eventMember.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Map<String, Object> templateContext = new HashMap<>();
        // Populate template context with member and event details
        templateContext.put("имя", eventMember.getFirstname());
        templateContext.put("middlename", eventMember.getMiddlename());
        templateContext.put("last_name", eventMember.getLastname());
        templateContext.put("email", eventMember.getEmail());
        templateContext.put("phone", eventMember.getPhone());
        templateContext.put("position", eventMember.getPosition());
        templateContext.put("company", eventMember.getCompany());
        templateContext.put("member_role", eventService.getRoleNameById(eventMember.getEventMembersRoleId()));

        templateContext.put("event_date", event.getEvent_date());
        templateContext.put("event_name", event.getEvent_name());
        templateContext.put("event_summary", event.getEvent_summary());
        templateContext.put("event_address", event.getAddress());

        AbstractEmailContext context = new EmailContext();
        context.setContext(templateContext);
        context.setTo(eventMember.getEmail());
        context.setSubject("Регистрация на мероприятие: " + event.getEvent_name());
        context.setTemplateLocation("templates/greetings.html");

        try {
            emailService.sendMail(context);
            return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
        } catch (MessagingException e) {
            LOG.error("Error while sending out email..", e);
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //    @GetMapping(value = "/simple-order-email/{user-email}")
//    public @ResponseBody ResponseEntity sendEmailAttachment(@PathVariable UUID memberId) throws IOException {
//        Event_Member eventMember = eventService.findMemberById(memberId)
//                .orElseThrow(() -> new RuntimeException("Event member not found"));
//        Event event = eventService.findById(eventMember.getEventId())
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//
//        Map<String, Object> templateContext = new HashMap<>();
//        templateContext.put("first_name", eventMember.getFirstname());
//        templateContext.put("middlename", eventMember.getMiddlename());
//        AbstractEmailContext context = new EmailContext();
//        context.setContext(templateContext);
//        context.setTo(eventMember.getEmail());
//        context.setSubject("Регистрация на мероприятие: " + event.getEvent_name());
//        context.setTemplateLocation("greetings.html");
//        try {
//            emailService.sendMail(context);
//        } catch (MessagingException e) {
//            LOG.error("Error while sending out email..{}", e.getStackTrace());
//            LOG.error("Error while sending out email..{}", e.fillInStackTrace());
//            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
//    }

    //    @GetMapping(value = "/simple-email/{user-email}")
//    public @ResponseBody ResponseEntity sendSimpleEmail(@PathVariable("user-email") String email) {
//        try {
//            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for your!!");
//        } catch (MailException mailException) {
//            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
//            LOG.error("Error while sending out email..{}", mailException.fillInStackTrace());
//            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
//    }

}