package com.rebalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        // 這行指令會啟動一個內建的 Tomcat 伺服器，監聽 8080 埠口
        SpringApplication.run(MainApp.class, args);
    }
}