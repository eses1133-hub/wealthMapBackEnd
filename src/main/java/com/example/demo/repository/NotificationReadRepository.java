package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.SystemNotificationRead;

@Repository
public interface NotificationReadRepository extends JpaRepository<SystemNotificationRead, Long> {

	long countByUserId(Long userId);
	boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);
	List<Long> findNotificationIdsByUserId(Long userId);
}