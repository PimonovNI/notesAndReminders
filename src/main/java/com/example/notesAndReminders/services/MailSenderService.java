package com.example.notesAndReminders.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    @Autowired
    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setSubject(subject);
        mail.setTo(to);
        mail.setFrom(username);
        mail.setText(message);

        mailSender.send(mail);
    }
}
