package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class WealthmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(WealthmapApplication.class, args);
	}
	
	//更新新聞用
	@SpringBootApplication
	@EnableScheduling // 💡 必須加上這行，排程才會生效
	public class DemoApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(DemoApplication.class, args);
	    }
	}
	
	//發送email同步使用
	@SpringBootApplication
	@EnableAsync // 💡 開啟異步執行功能
	public class WealthMapApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(WealthMapApplication.class, args);
	    }
	}

}
