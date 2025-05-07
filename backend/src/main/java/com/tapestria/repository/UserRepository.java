package com.tapestria.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tapestria.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByDisplayName(String displayName);
    @Query("SELECT u.email FROM User u WHERE u.role = 'LIBRARIAN'")
    List<String> findAllLibrarianEmails();
    List<User> findByTotalFineGreaterThan(BigDecimal totalFine);
    @Query("SELECT COALESCE(SUM(u.totalFine), 0) FROM User u")
    BigDecimal sumTotalFines();
}
