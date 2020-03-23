package de.adesso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final ConfigService configService;

    private JavaMailSender emailSender;

    @Autowired
    public EmailService(ConfigService configService, @Qualifier("newMailSender") JavaMailSender emailSender) {
        this.configService = configService;
        this.emailSender = emailSender;
    }

    public void sendSimpleEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(configService.getRECEIPIENT());
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