package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	//確認是否有註冊過
    Optional<User> findByEmail(String email);

}