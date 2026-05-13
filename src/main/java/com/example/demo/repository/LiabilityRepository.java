package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Liability;
import java.util.List;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {
    List<Liability> findByUser_Id(Long userId);
    
    // 根據繳款日與是否啟用提醒來搜尋
    List<Liability> findByDueDayAndNotifyEnabled(Integer dueDay, Boolean notifyEnabled);
}