package com.example.itemmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class ItemmanagementApplication {

	public static void main(String[] args) {
		
		Dotenv dotenv = Dotenv.load();

	    System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
	    System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		
		SpringApplication.run(ItemmanagementApplication.class, args);
	}

}

