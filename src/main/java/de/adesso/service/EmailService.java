package de.adesso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private Environment environment;

    @Value("${spring.mail.receipient}")
    private String RECEIPIENT = environment.getProperty("SPRING_MAIL_RECEIPIENT");;

    private JavaMailSender emailSender;

    public EmailService(@Autowired Environment environment,
                        @Qualifier("newMailSender") JavaMailSender emailSender) {
        this.environment = environment;
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