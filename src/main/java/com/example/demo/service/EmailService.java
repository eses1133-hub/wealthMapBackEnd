package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
   @Autowired
   private JavaMailSender mailSender;
   // 發送者信箱 (必須與 application.properties 中的 username 一致)
   private String fromEmail = "chssrtan789@gmail.com";
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
}

