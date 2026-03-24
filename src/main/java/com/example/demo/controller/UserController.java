package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserProfileDTO;
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
}