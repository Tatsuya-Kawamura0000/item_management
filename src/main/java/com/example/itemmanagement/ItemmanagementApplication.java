package com.example.itemmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class ItemmanagementApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		if (dotenv.get("MAIL_USERNAME") != null) {
			System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		}

		if (dotenv.get("MAIL_PASSWORD") != null) {
			System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		}

		SpringApplication.run(ItemmanagementApplication.class, args);
	}

}

