package com.example.demo.controller;

import com.example.demo.dto.RebalanceResponseDTO;
import com.example.demo.entity.RebalanceSetting;
import com.example.demo.repository.RebalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rebalance")
public class RebalanceController {

    @Autowired
    private RebalanceRepository rebalanceRepository;

    @GetMapping("/list/{userId}")
    public List<RebalanceResponseDTO> getList(@PathVariable("userId") Long userId) {
        // 將 Entity 轉換為 DTO 再回傳
        return rebalanceRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(entity -> new RebalanceResponseDTO(
                    entity.getId(),
                    entity.getSymbol(),
                    entity.getTargetPercentage(),
                    entity.getCurrentShares(),
                    entity.getUserId(),
                    entity.isActive()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/save")
    public RebalanceResponseDTO save(@RequestBody RebalanceSetting setting) {
        // 儲存 Entity
        RebalanceSetting saved = rebalanceRepository.save(setting);
        // 回傳 DTO
        return new RebalanceResponseDTO(
            saved.getId(),
            saved.getSymbol(),
            saved.getTargetPercentage(),
            saved.getCurrentShares(),
            saved.getUserId(),
            saved.isActive()
        );
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        rebalanceRepository.deleteById(id);
    }
}