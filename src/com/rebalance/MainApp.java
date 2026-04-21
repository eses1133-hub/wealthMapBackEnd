package com.rebalance;

import java.util.ArrayList;
import java.util.List;
import com.rebalance.model.Asset;
import com.rebalance.service.RebalanceService;

public class MainApp {
    public static void main(String[] args) {
        List<Asset> myPortfolio = new ArrayList<>();
        
        // 填入你截圖中的實際數據
        myPortfolio.add(new Asset("國泰永續高股息", 21.80, 22.62, 10000));
        myPortfolio.add(new Asset("群益台灣精選高息", 25.01, 23.27, 18000));
        myPortfolio.add(new Asset("鴻海", 205.68, 212.0, 4000));

        // 設定各 1/3 的目標比例
        double[] targets = {0.33, 0.33, 0.34};

        RebalanceService service = new RebalanceService();
        service.calculate(myPortfolio, targets);
    }
}