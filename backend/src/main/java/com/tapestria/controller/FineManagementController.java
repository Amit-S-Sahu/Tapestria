package com.tapestria.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tapestria.model.User;
import com.tapestria.repository.BookRepository;
import com.tapestria.repository.BorrowRepository;
import com.tapestria.repository.UserRepository;

@RestController
public class FineManagementController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("user/total-fine")
    public ResponseEntity<BigDecimal> getTotalFines() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(user.get().getTotalFine());
    }

    @GetMapping("admin/fine/{userId}")
    public ResponseEntity<BigDecimal> getFinesByUserId(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(user.get().getTotalFine());
    }

    @GetMapping("admin/total-fine")
    public ResponseEntity<BigDecimal> getAllTotalFines() {
        BigDecimal totalFine = userRepository.findAll().stream()
                .map(User::getTotalFine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(totalFine);
    }

    @PutMapping("admin/fine/{userId}/pay-fine")
    public ResponseEntity<String> payUserFine(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User foundUser = user.get();
        foundUser.setTotalFine(BigDecimal.ZERO);
        userRepository.save(foundUser);
        return ResponseEntity.ok("Fines paid successfully");
    }

    @PutMapping("user/fine/pay-fine")
    public ResponseEntity<String> payFine() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User foundUser = user.get();
        if (foundUser.getTotalFine().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(400).body("No fines to pay");
        }
        //! TODO: Implement payment logic here (e.g., payment gateway integration)
        foundUser.setTotalFine(BigDecimal.ZERO);
        userRepository.save(foundUser);
        return ResponseEntity.ok("Fines paid successfully");
    }
}
