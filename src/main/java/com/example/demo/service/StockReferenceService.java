package com.example.demo.service;

import com.example.demo.dto.TwStockListDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class StockReferenceService { 

    
    public TwStockListDTO getStockNameInfo(String symbol) {
        
        String stockName = switch (symbol) {
            case "2330" -> "台積電";
            case "0050" -> "元大台灣50";
            case "2317" -> "鴻海";
            case "2881" -> "富邦金";
            case "2603" -> "長榮";
            case "2454" -> "聯發科";
            default -> "未知股票(" + symbol + ")"; 
        };

        return new TwStockListDTO(
            symbol, 
            stockName, 
            "電子/金融/傳產", 
            LocalDateTime.now().toString()
        );
    }
}