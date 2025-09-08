package com.example.saratogapizza.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void afterTheRegister(String userEmail) throws MessagingException {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            String subject = "Registration Confirmation";
            String text = "Dear New Member Welcome to the Saratoga Pizza. We are so glad to see you here. " +
                    "\nCould you please complete your register process in that link --> "

                    +"\nHave a great day!";

            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setTo(userEmail);

            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("failed to send mail");
        }

    }

    public void sendVerificationEmail(String email, String code,String name,String lastname) throws MessagingException {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            String subject = "Registration Confirmation";
            String text = "Dear"+ name +" "+lastname+" Welcome to the Saratoga Pizza. We are so glad to see you here. " +
                    "\nCould you please complete your register with that code ->"+ code

                    +"\nHave a great day!";

            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setTo(email);

            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("failed to send mail");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationOfEmailChange(String email, String code,String name,String lastname) throws MessagingException {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            String subject = "Verify Your new email address";
            String text = "Dear"+ name +" "+lastname+" " +
                    "\nCould you please complete your change of email with that code ->"+ code

                    +"\nHave a great day!";

            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setTo(email);

            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("failed to send mail");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

