package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.dto.NotificationListDTO;
import com.example.demo.entity.Notification;
import com.example.demo.service.NotificationService;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class NotificationController {
	
	@Configuration
	public class WebConfig implements WebMvcConfigurer {

	    @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")
	                .allowedOrigins("http://localhost:4200") // ✅ 必須明確指定，不能用 "*"
	                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	                .allowedHeaders("*")
	                .allowCredentials(true) // ✅ 這行就是解決你報錯的關鍵
	                .maxAge(3600); // 預檢請求(Preflight)的快取時間
	    }
	}

    @Autowired
    private NotificationService service;

    /**
     * 1. 取得所有公告列表 (GET)
     * 用於前端渲染 notificationList 陣列
     */
    @GetMapping("/list")
    public AppResponse<List<Notification>> getList() {
        List<Notification> list = service.getNotificationList();
        return AppResponse.success(list);
    }

    /**
     * 2. 新增公告 (POST)
     * 💡 使用 @Valid 觸發 DTO 中的 @NotBlank 驗證
     */
    @PostMapping("/save")
    public AppResponse<Notification> save(@Valid @RequestBody NotificationListDTO dto) {
        // 新增時不應該帶有 ID
        dto.setId(null);
        Notification saved = service.saveNotification(dto);
        return AppResponse.success(saved);
    }

    /**
     * 3. 更新公告 (PUT)
     */
    @PutMapping("/update")
    public AppResponse<Notification> update(@Valid @RequestBody NotificationListDTO dto) {
        // 💡 檢查是否有提供 ID，若無則回傳你定義的 PARAM_ERROR
        if (dto.getId() == null) {
            return AppResponse.error(RspCode.PARAM_ERROR, "更新公告時必須提供 ID");
        }
        
        try {
            Notification updated = service.saveNotification(dto);
            return AppResponse.success(updated);
        } catch (RuntimeException e) {
            return AppResponse.error(RspCode.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 4. 刪除公告 (DELETE)
     * 透過 URL 傳入 ID，例如: /api/notifications/5
     */
    @DeleteMapping("/{id}")
    public AppResponse<Void> delete(@PathVariable("id") Long id) {
        try {
            service.deleteNotification(id);
            return AppResponse.success(null);
        } catch (RuntimeException e) {
            return AppResponse.error(RspCode.NOT_FOUND, e.getMessage());
        }
    }
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<Notification>> getNotificationById(@PathVariable("id") Long id) {
        Notification notification = notificationService.findById(id);
        
        if (notification != null) {
            // ✅ 使用你定義的 success 靜態方法，它會自動處理 RspCode.SUCCESS
            return ResponseEntity.ok(AppResponse.success(notification));
        } else {
            // ✅ 使用你定義的 error 靜態方法，傳入對應的錯誤列舉
            // 假設你的 RspCode 裡面有 NOT_FOUND 或 DATA_NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(AppResponse.error(RspCode.NOT_FOUND)); 
        }
    }
    
    
}