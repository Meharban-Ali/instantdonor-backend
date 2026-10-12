package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageBuilder {
    private static final Logger log = LoggerFactory.getLogger(EmailMessageBuilder.class);
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public MimeMessage buildHtmlMessage(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, 3, "UTF-8");
        helper.setFrom(this.mailProperties.getFromAddress(), this.mailProperties.getFromName());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        return message;
    }

    public SimpleMailMessage buildSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.mailProperties.getFromName() + " <" + this.mailProperties.getFromAddress() + ">");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    public EmailMessageBuilder(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }
}

