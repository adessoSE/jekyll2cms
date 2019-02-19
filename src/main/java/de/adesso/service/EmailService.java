package de.adesso.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.receipient}")
    private String RECEIPIENT;

    private JavaMailSender emailSender;

    public EmailService(@Qualifier("newMailSender") JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(RECEIPIENT);
        message.setSubject(subject);
        message.setText(text);
        try {
            this.emailSender.send(message);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}