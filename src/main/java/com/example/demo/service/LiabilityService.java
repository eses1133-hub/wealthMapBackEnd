package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Liability;
import com.example.demo.repository.LiabilityRepository;
import java.util.List;

@Service
public class LiabilityService {

    @Autowired
    private LiabilityRepository liabilityRepo;

    // 1. 新增負債
    public Liability createLiability(Liability liability) {
        return liabilityRepo.save(liability);
    }

    // 2. 查詢該使用者的所有負債
    public List<Liability> getLiabilitiesByUserId(Long userId) {
        return liabilityRepo.findByUserId(userId);
    }

    // 3. 刪除負債
    public void deleteLiability(Long id) {
        liabilityRepo.deleteById(id);
    }
}