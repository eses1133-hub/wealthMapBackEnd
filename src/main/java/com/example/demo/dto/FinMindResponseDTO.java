package com.example.demo.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinMindResponseDTO {
	private String msg;
	private int status;
	private List<StockDataDTO> data;
}
