package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.service.NotificationService;
import com.example.demo.service.SseService;

@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins = "http://localhost:4200") // 允許所有來源連線 (開發方便)
public class SseController {
	private final NotificationService notificationService;

	@Autowired
	private SseService sseService;
	
	public SseController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	// Client 端連線端點: GET /api/sse/subscribe/{userId}
	@GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
	public SseEmitter subscribe(@PathVariable("userId") String userId) {
		System.out.println("使用者訂閱: " + userId);
		return sseService.subscribe(userId);
	}

	// 觸發通知端點 (模擬後台發送): POST /api/sse/send?userId=gaga&message=hello
	@GetMapping("/send")
	public String send(@RequestParam("userId") String userId, 
			           @RequestParam("message") String message) {
			
		notificationService.sendNotification(userId, message);
		return "Message sent to " + userId;
	}
}
