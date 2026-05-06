package com.example.demo.service;

import com.example.demo.dto.CashFlowDTO;
import com.example.demo.entity.CashFlow;
import com.example.demo.repository.CashFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashFlowService {

    @Autowired
    private CashFlowRepository cashFlowRepository;

    // 查詢某使用者所有收支
    public List<CashFlowDTO> getByUserId(Long userId) {
        return cashFlowRepository.findByUserId(userId)
            .stream()
            .map(cf -> new CashFlowDTO(
                cf.getId(),
                cf.getUserId(),
                cf.getType(),
                cf.getCategory(),
                cf.getAmount(),
                cf.getDescription(),
                cf.getRecordDate()
            ))
            .collect(Collectors.toList());
    }

    // 新增一筆收支
    public CashFlowDTO addRecord(CashFlowDTO dto) {
        CashFlow entity = new CashFlow();
        entity.setUserId(dto.userId());
        entity.setType(dto.type());
        entity.setCategory(dto.category());
        entity.setAmount(dto.amount());
        entity.setDescription(dto.description());
        entity.setRecordDate(dto.recordDate());

        CashFlow saved = cashFlowRepository.save(entity);

        return new CashFlowDTO(
            saved.getId(),
            saved.getUserId(),
            saved.getType(),
            saved.getCategory(),
            saved.getAmount(),
            saved.getDescription(),
            saved.getRecordDate()
        );
    }

    // 刪除一筆收支
    public void deleteRecord(Long id) {
        cashFlowRepository.deleteById(id);
    }
}