package com.example.demo.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
	private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();

	public SseEmitter subscribe(String userId) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

		clients.put(userId, emitter);

		emitter.onCompletion(() -> clients.remove(userId));
		emitter.onTimeout(() -> clients.remove(userId));

		
		return emitter;
	}
	
	public void sendMessage(Long userId, String message) {
		SseEmitter emitter = clients.get(userId);
		
		if(emitter != null) {
			try {
				emitter.send(SseEmitter.event().data(message));
			}catch (Exception e) {
				clients.remove(userId);
			}
		}
	}
}
