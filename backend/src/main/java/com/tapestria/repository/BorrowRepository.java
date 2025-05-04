package com.tapestria.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tapestria.model.Borrow;

public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    List<Borrow> findByEmail(String email);
    List<Borrow> findByIsbn(String isbn);
    List<Borrow> findByReturnDateIsNull();
    List<Borrow> findByReturnDateIsNullAndEmail(String email);
    List<Borrow> findByReturnDateIsNullAndDueDateBefore(Date date);
    List<Borrow> findByReturnDateIsNullAndDueDate(Date date);
    List<Borrow> findByReturnDateIsNullAndDueDateBeforeAndEmail(Date date, String email);
}
