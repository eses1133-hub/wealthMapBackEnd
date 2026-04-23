package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.SystemNotificationRead;

@Repository
public interface NotificationReadRepository extends JpaRepository<SystemNotificationRead, Long> {

	long countByUserId(Long userId);
	boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);
	
	@Query("SELECT n.notificationId FROM SystemNotificationRead n WHERE n.userId = :userId")
	List<Long> findNotificationIdsByUserId(@Param("userId")Long userId);
}