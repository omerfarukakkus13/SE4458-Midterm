package com.shortstay.api;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShortStayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortStayApiApplication.class, args);
	}

}
