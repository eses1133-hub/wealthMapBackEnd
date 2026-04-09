package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "asset_parameters")
@Data
public class AssetParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_name")
    private String assetName; 

    @Column(name = "avg_return")
    private BigDecimal avgReturn;

    @Column(name = "std_dev")
    private BigDecimal stdDev;

    private String description;
}