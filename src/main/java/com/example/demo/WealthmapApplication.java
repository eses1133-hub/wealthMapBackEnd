package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//更新新聞用
@SpringBootApplication
@EnableScheduling
public class WealthmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(WealthmapApplication.class, args);
		
	}
	
}
