package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashFlowDTO(
    Long id,
    Long userId,
    String type,
    String category,
    BigDecimal amount,
    String description,
    LocalDate recordDate
) {}