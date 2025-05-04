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
}
