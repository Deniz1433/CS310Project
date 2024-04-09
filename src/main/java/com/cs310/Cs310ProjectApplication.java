package com.cs310;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Cs310ProjectApplication implements CommandLineRunner{
	
	Logger logger = LoggerFactory.getLogger(Cs310ProjectApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(Cs310ProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		
		logger.info("Hello World");
		
		
	}

}