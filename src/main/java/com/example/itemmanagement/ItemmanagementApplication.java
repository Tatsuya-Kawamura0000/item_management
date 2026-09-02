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

		if (dotenv.get("OPENAI_API_KEY") != null) {
			System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));
		}

		// ローカル開発用。本番は Render の環境変数を使う
		copyDotenvIfPresent(dotenv, "VAPID_PUBLIC_KEY");
		copyDotenvIfPresent(dotenv, "VAPID_PRIVATE_KEY");
		copyDotenvIfPresent(dotenv, "VAPID_SUBJECT");

		SpringApplication.run(ItemmanagementApplication.class, args);
	}

	private static void copyDotenvIfPresent(Dotenv dotenv, String key) {
		if (dotenv.get(key) != null) {
			System.setProperty(key, dotenv.get(key));
		}
	}

}

