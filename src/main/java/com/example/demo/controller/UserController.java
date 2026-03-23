package com.example.demo.controller;

<<<<<<< Updated upstream

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;	

    @GetMapping("/profile")
    public AppResponse<User> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return AppResponse.error(RspCode.UNAUTHORIZED);
        }
        
        String email = authentication.name();
        return userRepository.findByEmail(email)
                .map(AppResponse::success)
                .orElse(AppResponse.error(RspCode.NOT_FOUND, "User not found"));
    }

=======
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
	
	 @Autowired
	    private UserRepository userRepository;

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
>>>>>>> Stashed changes
}