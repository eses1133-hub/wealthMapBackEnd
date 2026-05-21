package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Asset;

@Repository
public interface AssetRecordRepository extends JpaRepository<Asset, Long> {
	List<Asset> findByUser_Id(Long userId);

	@Query("""
			SELECT DATE_FORMAT(a.createdAt, '%Y-%m') as month,
			       SUM(a.amount)
			FROM Asset a
			WHERE a.user.id = :userId
			GROUP BY DATE_FORMAT(a.createdAt, '%Y-%m')
			ORDER BY month
			""")
	List<Object[]> getMonthlyAssets(@Param("userId") Long userId);

}
