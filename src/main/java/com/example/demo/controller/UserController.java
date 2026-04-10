package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.StrategyResponseDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.entity.Asset;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;


import java.util.List;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;	

    @GetMapping("/profile")
    public AppResponse<User> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return AppResponse.error(RspCode.UNAUTHORIZED);
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(AppResponse::success)
                .orElse(AppResponse.error(RspCode.NOT_FOUND, "User not found"));
    }
    
 // 取得所有使用者
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 新增使用者
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    @GetMapping("/by-email")
    public AppResponse<UserProfileDTO> getUserByEmail(@RequestParam("email") String email) {

        return userRepository.findByEmail(email)
                .map(user -> {
                    UserProfileDTO dto = new UserProfileDTO();
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    return AppResponse.success(dto);
                })
                .orElse(AppResponse.error(RspCode.NOT_FOUND, "User not found"));
    }
    
    @GetMapping("/details")
    public AppResponse<UserProfileDTO> getUserDetailsByEmail(@RequestParam("email") String email) {

        return userRepository.findByEmailWithAllData(email) // 改用我們寫的優化查詢
                .map(user -> {
                    UserProfileDTO dto = new UserProfileDTO();
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole());

                    // 轉換資產 Assets
                    dto.setAssets(user.getAssets().stream().map(a -> {
                        UserProfileDTO.AssetDTO adto = new UserProfileDTO.AssetDTO();
                        adto.setId(a.getId());
                        adto.setName(a.getName());
                        adto.setSymbol(a.getSymbol());
                        adto.setType(a.getType()); 
                        adto.setAmount(a.getAmount());
                        return adto;
                    }).toList());
                    
                    // 轉換 Investments
                    dto.setInvestments(user.getInvestments().stream().map(i -> {
                        UserProfileDTO.InvestmentDTO idto = new UserProfileDTO.InvestmentDTO();
                        idto.setId(i.getId());
                        idto.setSymbol(i.getSymbol());
                        idto.setType(i.getType()); 
                        idto.setQuantity(i.getQuantity());
                        idto.setBuyPrice(i.getBuyPrice());
                        idto.setCurrentPrice(i.getCurrentPrice());
                        return idto;
                    }).toList());
                    
                    // 轉換 FinancialGoals
                    dto.setFinancialGoals(user.getFinancialGoals().stream().map(g -> {
                        UserProfileDTO.FinancialGoalDTO gdto = new UserProfileDTO.FinancialGoalDTO();
                        gdto.setId(g.getId());
                        gdto.setGoalName(g.getGoalName());
                        gdto.setTargetAmount(g.getTargetAmount()); 
                        gdto.setCurrentAmount(g.getCurrentAmount());
                        gdto.setTargetDate(g.getTargetDate());
                        return gdto;
                    }).toList());

                    // 轉換 StrategySettings                    
                    dto.setStrategySettings(user.getStrategySettings().stream().map(s -> 
	                    new StrategyResponseDTO(
	                        s.getId(),
	                        s.getSymbol(),
	                        s.getBuyThreshold(),
	                        s.getSellThreshold(),
	                        s.isActive(),        
	                        user.getId() 
	                    )
	                ).toList());
                    
                    return AppResponse.success(dto);
                })
                .orElse(AppResponse.error(RspCode.NOT_FOUND, "找不到該使用者資料"));
    }
}