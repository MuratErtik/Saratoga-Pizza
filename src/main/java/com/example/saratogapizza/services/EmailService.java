package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Order;
import com.example.saratogapizza.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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


    public void resetPasswordMail(User user,String url) throws MessagingException {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            String subject = "Password Reset Process";
            String text = "Dear"+ user.getName() +" "+user.getLastname()+" Welcome to the Saratoga Pizza. " +
                    "\nCould you please complete your password-reset  with this url ->"+ url

                    +"\nHave a great day!";

            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setTo(user.getEmail());

            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("failed to send mail");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMailAfterTheOrderToCustomer(Order order) throws MessagingException {
        try {
            User user = order.getCart().getUser();
            String fullName = user.getName() + " " + user.getLastname();

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Your Saratoga Pizza Order Confirmation";
            String text = "Dear " + fullName + ",\n\n"
                    + "Thank you for ordering from Saratoga Pizza!\n"
                    + "We’ve received your order and it’s being prepared right now.\n\n"
                    + "**Order Details**\n"
                    + "• Order ID: " + order.getId() + "\n"
                    + "• Order Date: " + order.getOrderDate() + "\n"
                    + "• Delivery Address: " + order.getShippingAddress().getAddressName() + "\n"
                    + "• Estimated Delivery Time: " + order.getDeliverDate() + "\n"
                    + "• Total Amount: $" + order.getCart().getTotalSellingPrice() + "\n\n"
                    + "You will receive another email once your order is out for delivery.\n\n"
                    + "Thank you for choosing Saratoga Pizza \n"
                    + "Have a great day!";

            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setTo(user.getEmail());
            messageHelper.setFrom("no-reply@saratogapizza.com");

            javaMailSender.send(mimeMessage);
            log.info("Order confirmation email sent successfully to {}", user.getEmail());

        } catch (MailException e) {
            log.error("Failed to send order email: {}", e.getMessage());
            throw new MailSendException("Failed to send order confirmation email");
        } catch (MessagingException e) {
            log.error("Messaging exception while sending order email: {}", e.getMessage());
            throw new RuntimeException("Email message creation failed", e);
        }
    }

}

