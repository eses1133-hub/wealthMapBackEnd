package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.MonteCarloSimulation;

@Repository
public interface MonteRepository extends JpaRepository<MonteCarloSimulation, Long>{
	
}
