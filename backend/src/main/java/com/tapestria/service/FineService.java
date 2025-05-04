package com.tapestria.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tapestria.model.Borrow;
import com.tapestria.model.User;
import com.tapestria.repository.BorrowRepository;
import com.tapestria.repository.UserRepository;

@Service
public class FineService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void calculateFines() {
        List<Borrow> overdueBorrows = borrowRepository.findByReturnDateIsNullAndDueDateBefore(new Date());

        for (Borrow borrow : overdueBorrows) {
            BigDecimal fineAmount = calculateFine(borrow.getDueDate(), new Date());
            borrow.setFineAmount(fineAmount);
            borrowRepository.save(borrow);
        }

        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Borrow> userBorrows = borrowRepository.findByEmail(user.getEmail());
            BigDecimal totalFine = userBorrows.stream()
                    .map(Borrow::getFineAmount)
                    .filter(f -> f != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            user.setTotalFine(totalFine);
            userRepository.save(user);
        }
    }

    private BigDecimal calculateFine(Date dueDate, Date currentDate) {
        long diffInMillies = currentDate.getTime() - dueDate.getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        if (diffInDays <= 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(diffInDays).multiply(new BigDecimal("5.00"));
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendEmailReminders() {
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + 86400000);
        Date yesterday = new Date(today.getTime() - 86400000);

        List<Borrow> dueTomorrow = borrowRepository.findByReturnDateIsNullAndDueDate(tomorrow);
        List<Borrow> overdue = borrowRepository.findByReturnDateIsNullAndDueDate(yesterday);
        List<Borrow> dueToday = borrowRepository.findByReturnDateIsNullAndDueDate(today);

        for (Borrow borrow : dueTomorrow) {
            User user = userRepository.findByEmail(borrow.getEmail()).orElse(null);
            if (user != null) {
                String subject = "Book Due Tomorrow";
                String message = "Dear " + user.getDisplayName() + ",\n\nYour borrowed book is due tomorrow. Please return it on time.\n\nThank you!";
                emailService.sendEmail(user.getEmail(), subject, message);
            }
        }

        for (Borrow borrow : overdue) {
            User user = userRepository.findByEmail(borrow.getEmail()).orElse(null);
            if (user != null) {
                String subject = "Book Overdue";
                String message = "Dear " + user.getDisplayName() + ",\n\nYour borrowed book is overdue. Please return it as soon as possible.\n\nThank you!";
                emailService.sendEmail(user.getEmail(), subject, message);
            }
        }

        for (Borrow borrow : dueToday) {
            User user = userRepository.findByEmail(borrow.getEmail()).orElse(null);
            if (user != null) {
                String subject = "Book Due Today";
                String message = "Dear " + user.getDisplayName() + ",\n\nYour borrowed book is due today. Please return it on time.\n\nThank you!";
                emailService.sendEmail(user.getEmail(), subject, message);
            }
        }

        List<User> usersWithFine = userRepository.findByTotalFineGreaterThan(new BigDecimal("100.00"));
        for (User user : usersWithFine) {
            String subject = "Outstanding Fine Reminder";
            String message = "Dear " + user.getDisplayName() + ",\n\nYou have an outstanding fine of " + user.getTotalFine() + ". Please clear it at your earliest convenience.\n\nThank you!";
            emailService.sendEmail(user.getEmail(), subject, message);
        }

        List<User> usersWithLibrarianRole = userRepository.findByRole("LIBRARIAN");
        List<Borrow> overdueBorrows = borrowRepository.findByReturnDateIsNullAndDueDate(yesterday);
        for (User user : usersWithLibrarianRole) {
            String subject = "Books Due Yesterday";
            String message = "Dear " + user.getDisplayName() + ",\n\nThe following books were due yesterday:\n";
            for (Borrow borrow : overdueBorrows) {
                message += "- " + borrow.getIsbn() + "\n" + "  Borrowed by: " + borrow.getEmail() + "\n";
            }
            message += "\nPlease follow up with the respective users.\n\nThank you!";
            emailService.sendEmail(user.getEmail(), subject, message);
        }
    }
}
