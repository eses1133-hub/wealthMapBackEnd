package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.EmailService;

@RestController
public class EmailController {
	@Autowired
	private EmailService emailService;

	// 測試網址: http://localhost:8080/send-mail?to=收件人@example.com
	@GetMapping("/send-mail")
	public String sendTestMail(@RequestParam("to") String to) {
		try {
			emailService.sendSimpleEmail(to, "Spring Boot 測試郵件", "你好！這是一封來自 Spring Boot 的測試信件。");
			return "發送成功！請檢查信箱。";
		} catch (Exception e) {
			e.printStackTrace();
			return "發送失敗: " + e.getMessage();
		}
	}


	@GetMapping("/")
	public String home() {
		return "Welcome to the Email Service!";
	}

}
