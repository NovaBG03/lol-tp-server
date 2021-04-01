package web.app.loltp.loltpserver.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import web.app.loltp.loltpserver.exceptions.LolTpException;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${lol.tp.email}")
    private String lolTpEmail;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void askQuestion(String email, String name, String question) {
        this.validateEmail(email);

        this.sendEmail(email, "Question sent to LoLTP", "Your question: " + question);
        this.sendEmailToLoLTp("Question from " + name, "Sent by: " + email + "\n" + question);
    }

    public void sendEmailToLoLTp(String subject, String content) {
        this.sendEmail(lolTpEmail, subject, content);
    }

    public void sendEmail(String email, String subject, String content) {
        this.validateEmail(email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(lolTpEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);

        try {
            emailSender.send(message);
        } catch (MailException me) {
            throw new LolTpException("CANNOT_SEND", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateEmail(String email) {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        if (!email.matches(regex)) {
            throw new LolTpException("INVALID_EMAIL", HttpStatus.BAD_REQUEST);
        }
    }
}
