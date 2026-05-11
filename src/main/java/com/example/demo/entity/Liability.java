package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "liabilities")
@Data
@NoArgsConstructor  // 自動生成無參數建構子
@AllArgsConstructor // 自動生成全參數建構子
public class Liability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;      // 項目名稱 (例如：XX銀行房貸、車貸)

    @Column(nullable = false)
    private String category;  // 分類 (例如：房貸、車貸、信貸、學貸)

    @Column(nullable = false)
    private Double amount;    // 負債金額
    
    @Column(nullable = false)
    private double monthlyPayment;  // 月還款

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore //一開始就把防護罩加上，杜絕無限迴圈！
    private User user;

}