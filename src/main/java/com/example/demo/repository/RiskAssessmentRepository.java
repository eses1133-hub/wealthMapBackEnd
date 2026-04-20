package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RiskAssessment;

@Repository
// 💡 繼承了 JpaRepository，Spring Boot 才會自帶 save() 功能！
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
}