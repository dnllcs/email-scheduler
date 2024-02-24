package com.example.emailAttachments.application;

import java.util.List;

public record EmailRequest(String to, String subject, String body, List<String> attachments) {
}
