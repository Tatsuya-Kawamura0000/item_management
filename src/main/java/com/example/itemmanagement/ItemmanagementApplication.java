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

		if (dotenv.get("SENDGRID_API_KEY") != null) {
			System.setProperty("SENDGRID_API_KEY", dotenv.get("SENDGRID_API_KEY"));
		}

		SpringApplication.run(ItemmanagementApplication.class, args);
	}

}

