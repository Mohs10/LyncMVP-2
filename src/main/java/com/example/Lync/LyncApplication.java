package com.example.Lync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(LyncApplication.class, args);
	}

}
