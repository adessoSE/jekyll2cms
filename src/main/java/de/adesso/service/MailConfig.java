package de.adesso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private final ConfigService configService;

    @Autowired
    public MailConfig(ConfigService configService) {
        this.configService = configService;
    }

    @Bean
    @Qualifier("newMailSender")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configService.getSPRING_MAIL_HOST());
        mailSender.setPort(Integer.parseInt(configService.getSPRING_MAIL_PORT()));

        mailSender.setUsername(configService.getSPRING_MAIL_USERNAME());
        mailSender.setPassword(configService.getSPRING_MAIL_PASSWORD());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", configService.getSPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH());
        props.put("mail.smtp.starttls.enable", configService.getSPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE());
        props.put("mail.debug", "true");

        return mailSender;
    }

}