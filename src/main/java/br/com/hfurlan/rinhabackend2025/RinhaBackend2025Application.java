package br.com.hfurlan.rinhabackend2025;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class RinhaBackend2025Application {

	public static void main(String[] args) {
		SpringApplication.run(RinhaBackend2025Application.class, args);
	}

}
