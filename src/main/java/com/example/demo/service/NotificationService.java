package com.example.demo.service;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.NotificationListDTO;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;

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

	// 這裡是系統發送通知的service
	@Autowired
    private NotificationRepository repository;

    // 1. 取得列表
    public List<Notification> getNotificationList() {
        return repository.findAllByOrderByScheduledDateDesc();
    }

    // 2. 儲存或更新
    public Notification saveNotification(NotificationListDTO dto) {
        Notification entity;

        if (dto.getId() != null) {
            entity = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("找不到該筆公告"));
        } else {
            entity = new Notification();
        }

        entity.setTag(dto.getTag());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());

        // 💡 關鍵就在這行！ 
        // 你的 DTO 變數叫 scheduledDate，所以這裡必須是 getScheduledDate()
        // 絕對不能出現 getScheduledTime()
        entity.setScheduledDate(dto.getScheduledDate() == null ? 
                               LocalDate.now() : dto.getScheduledDate());

        return repository.save(entity);
    }

    // 3. 刪除
    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }
    
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification findById(Long id) {
        // 使用 .orElse(null) 處理找不到資料的情況
        return repository.findById(id).orElse(null);
    }
}
