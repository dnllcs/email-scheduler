package com.example.emailAttachments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmailAttachmentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailAttachmentsApplication.class, args);
	}

}
