package com.example.demo.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationService {
	// 核心：用來存放 userId -> SseEmitter 的對應關係
	// 使用 ConcurrentHashMap 確保執行緒安全
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	// 1. 用戶訂閱 (建立連線)
	public SseEmitter subscribe(String userId) {
		// 設定超時時間，0 表示無限 (或設定例如 30分鐘: 1800000L)
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		// 存入 Map
		emitters.put(userId, emitter);
		// 連線結束或超時時，從 Map 中移除
		emitter.onCompletion(() -> emitters.remove(userId));
		emitter.onTimeout(() -> emitters.remove(userId));
		emitter.onError((e) -> emitters.remove(userId));
		try {
			// 發送一個初始事件，確認連線成功
			emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
		} catch (IOException e) {
			emitters.remove(userId);
		}
		return emitter;
	}

	// 2. 發送通知給特定用戶
	public void sendNotification(String userId, String message) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event().name("message") // 前端監聽的事件名稱
						.data(message));
			} catch (IOException e) {
				// 發送失敗通常代表連線已斷，移除該 Emitter
				emitters.remove(userId);
			}
		}
	}
}
