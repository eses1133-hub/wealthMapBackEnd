package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Liability;
import java.util.List;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {
    // 💡 自訂功能：靠著 userId 去把這個人的所有負債都找出來
    List<Liability> findByUserId(Long userId);
}