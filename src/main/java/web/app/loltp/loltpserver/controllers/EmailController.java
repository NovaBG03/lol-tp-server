package web.app.loltp.loltpserver.controllers;

import org.springframework.web.bind.annotation.*;
import web.app.loltp.loltpserver.dtos.EmailInfoDto;
import web.app.loltp.loltpserver.services.EmailService;


@CrossOrigin("*")
@RestController
@RequestMapping("email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("send")
    public String SendEmail(@RequestBody EmailInfoDto emailInfoDto) {
        emailService.askQuestion(emailInfoDto.getEmail(), emailInfoDto.getName(), emailInfoDto.getMessage());

        return "Ok!";
    }
}
