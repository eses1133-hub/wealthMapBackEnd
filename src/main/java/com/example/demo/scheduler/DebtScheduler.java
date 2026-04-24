package com.example.demo.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Debt;
import com.example.demo.repository.DebtRepository;
import com.example.demo.service.DebtService;
import com.example.demo.service.EmailService;
//import com.example.demo.service.Notification;
import com.example.demo.service.NotificationService;
import com.example.demo.service.SseService;

@Component
public class DebtScheduler {

	@Autowired
	private DebtRepository debtRepository;

	@Autowired
	private DebtService debtService;

	@Autowired
	private SseService sseService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private NotificationService notificationService;

	@Scheduled(fixedRate = 86400000) // 每10秒跑一次(測試用)
	public void checkDueDebts() {

		int today = LocalDate.now().getDayOfMonth();

		List<Debt> debts = debtRepository.findByDueDayAndNotifyEnabledAndActive(today, true, true);

		System.out.println("今天要提醒的人數：" + debts.size());

		for (Debt debt : debts) {

			double months = debtService.calculateRemainingMonths(debt);

			String message = "【繳款提醒】" + debt.getDebtName() + "本月需繳款，預計剩餘" + (int) months + "個月可還清";

			Long userId = debt.getUser().getId();

			
			String email = debt.getUser().getEmail();
			System.out.println("發送給 userId: " + userId + " | 訊息: " + message);
			
			//SSE發送
			sseService.sendMessage(userId, message);
			
			//Email發送
			emailService.sendSimpleEmail(email, "繳款提醒", message);
			
			//存入DB
			notificationService.saveAlertLog(userId, message);
		}
	}

}
