package ru.michaelshell.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.michaelshell.taskmanager.config.EmailProperties;
import ru.michaelshell.taskmanager.model.dto.EmailDetails;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public void send(EmailDetails details) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailProperties.getFrom());
        mailMessage.setTo(details.recipient());
        mailMessage.setText(details.msgBody());
        mailMessage.setSubject(details.subject());
        mailSender.send(mailMessage);
    }
}
