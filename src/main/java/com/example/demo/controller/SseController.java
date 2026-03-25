package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.service.NotificationService;
@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins = "*") // 允許所有來源連線 (開發方便)
public class SseController {
	private final NotificationService notificationService;

	public SseController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	// Client 端連線端點: GET /api/sse/subscribe/{userId}
	@GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
	public SseEmitter subscribe(@PathVariable("userId") String userId) {
		return notificationService.subscribe(userId);
	}

	// 觸發通知端點 (模擬後台發送): POST /api/sse/send?userId=gaga&message=hello
	@PostMapping("/send")
	public String send(@RequestParam("userId") String userId, @RequestParam("message") String message) {
		notificationService.sendNotification(userId, message);
		return "Message sent to " + userId;
	}
}
