package com.example.demo.repository; // 修正 package 路徑

import com.example.demo.entity.RebalanceSetting; // 修正 import 路徑
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RebalanceRepository extends JpaRepository<RebalanceSetting, Long> {
    List<RebalanceSetting> findByUserIdAndIsActiveTrue(Long userId);
}