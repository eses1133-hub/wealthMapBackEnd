package com.example.demo.controller; // 修正 package 路徑

import com.example.demo.entity.RebalanceSetting; // 修正 import
import com.example.demo.repository.RebalanceRepository; // 修正 import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rebalance")
public class RebalanceController {

    @Autowired
    private RebalanceRepository rebalanceRepository;

    @GetMapping("/list/{userId}")
    public List<RebalanceSetting> getList(@PathVariable("userId") Long userId) {
        return rebalanceRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @PostMapping("/save")
    public RebalanceSetting save(@RequestBody RebalanceSetting setting) {
        return rebalanceRepository.save(setting);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        rebalanceRepository.deleteById(id);
    }
}