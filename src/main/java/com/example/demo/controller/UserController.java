package com.example.demo.controller;

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
}