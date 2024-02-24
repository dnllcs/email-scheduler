package com.example.emailAttachments.adapter.web;

import com.example.emailAttachments.domain.model.EmailRequest;
import com.example.emailAttachments.port.in.SendEmailUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/email")
public class EmailController {

    private final SendEmailUseCase sendEmailUseCase;

    public EmailController(SendEmailUseCase sendEmailUseCase) {
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMail(@RequestBody EmailRequest request) {
        sendEmailUseCase.send(request);
    }
}
