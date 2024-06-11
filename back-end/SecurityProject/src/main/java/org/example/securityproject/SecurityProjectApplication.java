package org.example.securityproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.UUID;

@SpringBootApplication
public class SecurityProjectApplication {

	private static final Logger logger = LoggerFactory.getLogger(SecurityProjectApplication.class);

	public static void main(String[] args) {
		String eventId = UUID.randomUUID().toString();
		MDC.put("eventId", eventId);
		try {
			SpringApplication.run(SecurityProjectApplication.class, args);
		} finally {
			MDC.remove("eventId");
		}
	}

}
