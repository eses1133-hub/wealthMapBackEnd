package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NotificationListDTO {
	
		
	private Long id; // 公告ID (更新時需要提供)

    @NotBlank(message = "公告標籤不能為空")
    @Size(max = 10, message = "標籤長度不能超過10個字")
    private String tag; // 例如：[系統]、[活動]

    @NotBlank(message = "公告標題不能為空")
    @Size(max = 100, message = "公告標題不能超過100字")
    private String title; // 公告標題

    @NotBlank(message = "公告內容不能為空")
    private String content; // 公告內容

    private LocalDate scheduledDate; //排程日期
}
