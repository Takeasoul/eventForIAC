package com.events.controller;


import com.events.generator.QRCodeGenerator;
import com.events.service.EmailService;
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


import java.io.File;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/email")
@CrossOrigin()
@RequiredArgsConstructor
public class EmailController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);


    private final EmailService emailService;

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
    @GetMapping(value = "/{user-email}")
    public @ResponseBody ResponseEntity sendEmail(@PathVariable("user-email") String email) {

        AbstractEmailContext context = new EmailContext();
        context.setTo(email);
        context.setSubject("Приглашение на меропритие");
        context.setTemplateLocation("email_message.html");
        try {
            emailService.sendMail(context);
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