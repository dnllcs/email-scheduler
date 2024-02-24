package com.example.emailAttachments.application.web;

import com.example.emailAttachments.application.EmailRequest;
import com.example.emailAttachments.application.port.in.SendEmail;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/email")
public class EmailController {

    private final SendEmail sendEmailUseCase;

    public EmailController(SendEmail sendEmailUseCase) {
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMail(@RequestBody EmailRequest request) {
        sendEmailUseCase.send(request);
    }
}
