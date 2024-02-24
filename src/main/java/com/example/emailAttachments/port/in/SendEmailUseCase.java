package com.example.emailAttachments.port.in;

import com.example.emailAttachments.domain.model.EmailRequest;

public interface SendEmailUseCase {
    void send(EmailRequest request);
}
