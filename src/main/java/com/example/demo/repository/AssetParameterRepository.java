package com.example.demo.repository;

import com.example.demo.entity.AssetParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AssetParameterRepository extends JpaRepository<AssetParameter, Long> {
    Optional<AssetParameter> findByAssetName(String assetName);
}