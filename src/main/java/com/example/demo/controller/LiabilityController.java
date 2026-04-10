package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Liability;
import com.example.demo.entity.User;
import com.example.demo.service.LiabilityService;
import java.util.List;

@RestController
@RequestMapping("/api/liabilities") // 💡 前端呼叫的 API 開頭
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LiabilityController {

    @Autowired
    private LiabilityService liabilityService;

    // 1. 取得某使用者的所有負債 (GET /api/liabilities/user/{userId})
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Liability>> getUserLiabilities(@PathVariable Long userId) {
        return ResponseEntity.ok(liabilityService.getLiabilitiesByUserId(userId));
    }

    // 2. 新增負債 (POST /api/liabilities/user/{userId})
    @PostMapping("/user/{userId}")
    public ResponseEntity<Liability> addLiability(@PathVariable Long userId, @RequestBody Liability liability) {
        User user = new User();
        user.setId(userId);
        liability.setUser(user); // 綁定這是哪位會員的負債
        return ResponseEntity.ok(liabilityService.createLiability(liability));
    }

    // 3. 刪除負債 (DELETE /api/liabilities/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLiability(@PathVariable Long id) {
        liabilityService.deleteLiability(id);
        return ResponseEntity.ok().build();
    }
}