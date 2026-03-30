package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AssetRecord;
import com.example.demo.service.AssetService;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
	
	//自動注入 AssetService 服務層
	@Autowired
	private AssetService assetService;
	
	//接收前端的 JSON → 轉成 AssetRecord → 存進資料庫 → 把結果回傳給前端
	@PostMapping	
    public AssetRecord create(@RequestBody AssetRecord record){
        return assetService.save(record);
	}
}
