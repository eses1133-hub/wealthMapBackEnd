package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AlertLog;
import com.example.demo.entity.AlertLog.AlertStatus;
import com.example.demo.repository.AlertLogRepository;
@Service
public class EmailService {
	@Autowired
   private JavaMailSender mailSender;
	@Autowired
    private AlertLogRepository alertLogRepository;
   // 發送者信箱 (必須與 application.properties 中的 username 一致)
   private String fromEmail = "jingyi861211@gmail.com";
   /**
    * 發送純文字 Email
    * @param to 收件人 Email
    * @param subject 主旨
    * @param body 內容
    */

   public void sendSimpleEmail(String to, String subject, String body) {
       SimpleMailMessage message = new SimpleMailMessage();
      
       message.setFrom(fromEmail);
       message.setTo(to);
       message.setSubject(subject);
       message.setText(body);
       mailSender.send(message);
      
       System.out.println("郵件已發送至: " + to);
   }
   
   public void sendStrategyEmail(String toEmail, AlertLog logEntry) {
	// 1. 建立郵件內容
       SimpleMailMessage message = new SimpleMailMessage();
       message.setFrom(fromEmail);
       message.setTo(toEmail);
       message.setSubject(logEntry.getTitle());
       message.setText(logEntry.getContent());

       try {
           // 2. 寄信
           mailSender.send(message);
           // 3. 成功：改為 SENT
           logEntry.setStatus(AlertStatus.SENT);
       } catch (Exception e) {
           // 4. 失敗：改為 FAILED 並存下原因
           logEntry.setStatus(AlertStatus.FAILED);
           logEntry.setErrorMessage(e.getMessage());
       } finally {
           // 5. 更新資料庫
           alertLogRepository.save(logEntry);
       }
   }

}
