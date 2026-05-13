package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 自動生成無參數建構子
@AllArgsConstructor // 自動生成全參數建構子
public class UserAdminViewDTO {
	private Long id;
    private String name;
    private String email;
    private String role;
    private String riskLevel;
    private Boolean enabled;
}
