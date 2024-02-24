package com.example.emailAttachments.domain.model;

import java.util.List;

public record EmailRequest(String to, String subject, String body, List<String> attachments){}
