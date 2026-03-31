package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.HealthRequestDTO;
import com.example.demo.dto.HealthResponseDTO;

@Service
public class HealthService {

    public HealthResponseDTO calculate(HealthRequestDTO req) {

        double totalDebt =
                req.getMortgage()
                + req.getCarLoan()
                + req.getPersonalLoan()
                + req.getCreditCard();

        double L = req.getExpense() > 0
                ? (req.getSavings() + req.getCash()) / (double) req.getExpense()
                : 0;

        double DTI = req.getIncome() > 0
                ? (totalDebt / req.getIncome()) * 100
                : 0;

        double S = req.getIncome() > 0
                ? ((req.getIncome() - req.getExpense()) / (double) req.getIncome()) * 100
                : 0;

        double G = req.getInvestmentSuccessRate();

        int score = (int) ((L * 10 + (100 - DTI) + S + G) / 4);

        String level;
        if (score >= 80) level = "A";
        else if (score >= 60) level = "B";
        else level = "C";

        HealthResponseDTO res = new HealthResponseDTO();
        res.setL(L);
        res.setDTI(DTI);
        res.setS(S);
        res.setG(G);
        res.setScore(score);
        res.setLevel(level);

        return res;
    }
}