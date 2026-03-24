package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AssetRecord;

public interface AssetRecordRepository extends JpaRepository<AssetRecord, Long> {
	List<AssetRecord> findByUserId(Long userId);
}
