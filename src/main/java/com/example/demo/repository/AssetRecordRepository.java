package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Asset;

@Repository
public interface AssetRecordRepository extends JpaRepository<Asset, Long> {
	List<Asset> findByUser_Id(Long userId);
	
}
