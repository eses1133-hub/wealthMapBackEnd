package com.example.demo.controller;

import com.example.demo.dto.MonteDTO;
import com.example.demo.service.MonteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/monte")
@CrossOrigin(origins = "*") 
public class MonteController {
	
    @Autowired
    private MonteService monteService;

    @PostMapping("/simulate/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> simulate(
            @PathVariable("userId") Long userId,
            @RequestBody MonteDTO dto) {
    	Map<String, BigDecimal> result = monteService.calculateSimulation(
    			userId,               
                dto.getMonthly(),     
                dto.getYears(),         
                dto.getInitialAmount(), 
                dto.getAllocations(),   
                dto.getInflationRate()  
            );
    	return ResponseEntity.ok(result);
        }        
}