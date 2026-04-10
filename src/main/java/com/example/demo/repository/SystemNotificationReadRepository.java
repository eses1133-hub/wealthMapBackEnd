package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.SystemNotificationRead;

@Repository
public interface SystemNotificationReadRepository extends JpaRepository<SystemNotificationRead, Long> {
    
	// 算出某人讀了幾則
    long countByUserId(Long userId);
    
    // 檢查某人是否讀過某則
    boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);
    
    //從「已讀記錄表」中，根據 userId 撈出所有對應的 notificationId
    @Query("SELECT r.notificationId FROM SystemNotificationRead r WHERE r.userId = :userId")
    List<Long> findNotificationIdsByUserId(@Param("userId") Long userId);
}
