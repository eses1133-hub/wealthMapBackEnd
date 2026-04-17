package com.example.demo.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
	private String oldPassword; // 驗證臨時密碼（確保是本人操作）
    private String newPassword; // 設定新密碼
}
