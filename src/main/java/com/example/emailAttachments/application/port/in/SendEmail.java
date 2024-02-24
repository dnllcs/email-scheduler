package com.example.emailAttachments.application.port.in;

import com.example.emailAttachments.application.EmailRequest;

public interface SendEmail {
    void send(EmailRequest request);
}
