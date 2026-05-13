package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cash_flows")
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    // 類型：INCOME (收入) 或 EXPENSE (支出)
    @Column(name = "type")
    private String type;

    // 分類：例如 "薪水"、"餐飲"、"交通"
    @Column(name = "category")
    private String category;

    // 金額
    @Column(name = "amount")
    private BigDecimal amount;

    // 備註說明
    @Column(name = "description")
    private String description;

    // 記帳日期 (收入或支出發生的那一天)
    @Column(name = "record_date")
    private LocalDate recordDate;
}