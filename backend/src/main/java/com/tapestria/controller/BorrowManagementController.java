package com.tapestria.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tapestria.model.Book;
import com.tapestria.model.Borrow;
import com.tapestria.model.User;
import com.tapestria.repository.BookRepository;
import com.tapestria.repository.BorrowRepository;
import com.tapestria.repository.UserRepository;

@RestController
public class BorrowManagementController {
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("user/borrow-book")
    public ResponseEntity<Borrow> borrowBook(@RequestBody Borrow borrow) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(borrow.getIsbn()).orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.isAvailable()) {
            book.borrowBook();
            bookRepository.save(book);
            borrow.setEmail(email);
            borrow.setIssueDate(new Date());
            borrow.setDueDate(new Date(System.currentTimeMillis() + 604800000));
            borrowRepository.save(borrow);
            return ResponseEntity.ok(borrow);
        } 
        else return ResponseEntity.status(400).body(null);
    }

    @PostMapping("user/return-book")
    public ResponseEntity<Borrow> returnBook(@RequestBody Borrow borrow) {
        Borrow borrowBook = borrowRepository.findById(borrow.getBorrowId()).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        Book book = bookRepository.findById(borrowBook.getIsbn()).orElseThrow(() -> new RuntimeException("Book not found"));

        if (borrowRepository.existsById(borrow.getBorrowId())) {
            book.returnBook();
            bookRepository.save(book);
            borrowBook.setReturnDate(new Date());
            borrowRepository.save(borrowBook);
            return ResponseEntity.ok(borrowBook);
        } 
        else return ResponseEntity.status(400).body(null);
    }

    @PutMapping("user/renew-book")
    public ResponseEntity<Borrow> renewBook(@RequestBody Borrow borrow) {
        Borrow borrowBook = borrowRepository.findById(borrow.getBorrowId()).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        Book book = bookRepository.findById(borrowBook.getIsbn()).orElseThrow(() -> new RuntimeException("Book not found"));

        if (borrowRepository.existsById(borrow.getBorrowId())) {
            
            long elapsedMillis = System.currentTimeMillis() - borrowBook.getIssueDate().getTime();
            long elapsedDays = elapsedMillis / (1000 * 60 * 60 * 24);

            if (elapsedDays >= 21) return ResponseEntity.status(400).body(null);

            borrowBook.setDueDate(new Date(System.currentTimeMillis() + 604800000));
            borrowRepository.save(borrowBook);
            return ResponseEntity.ok(borrowBook);
        } 
        else return ResponseEntity.status(400).body(null);
    }

    @GetMapping("user/get-borrowed-books")
    public ResponseEntity<List<Borrow>> getBorrowedBooks() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Borrow> borrowedBooks = borrowRepository.findByReturnDateIsNullAndEmail(email);
        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("user/get-borrow-history")
    public ResponseEntity<List<Borrow>> getBorrowHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Borrow> borrowHistory = borrowRepository.findByEmail(email);
        return ResponseEntity.ok(borrowHistory);
    }

    @PostMapping("librarian/issue-book")
    public ResponseEntity<Borrow> issueBook(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getIsbn()).orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.isAvailable()) {
            book.borrowBook();
            bookRepository.save(book);
            borrow.setIssueDate(new Date());
            borrow.setDueDate(new Date(System.currentTimeMillis() + 604800000));
            borrowRepository.save(borrow);
            return ResponseEntity.ok(borrow);
        } 
        else return ResponseEntity.status(400).body(null);
    }

    @PostMapping("librarian/return-book")
    public ResponseEntity<Borrow> returnBookLibrarian(@RequestBody Borrow borrow) {
        Borrow borrowBook = borrowRepository.findById(borrow.getBorrowId()).orElseThrow(() -> new RuntimeException("Borrow record not found"));
        Book book = bookRepository.findById(borrowBook.getIsbn()).orElseThrow(() -> new RuntimeException("Book not found"));

        if (borrowRepository.existsById(borrow.getBorrowId())) {
            book.returnBook();
            bookRepository.save(book);
            borrowBook.setReturnDate(new Date());
            borrowRepository.save(borrowBook);
            return ResponseEntity.ok(borrowBook);
        } 
        else return ResponseEntity.status(400).body(null);
    }

    @GetMapping("librarian/get-borrowed-books-history")
    public ResponseEntity<List<Borrow>> getBorrowedBooksHistory() {
        List<Borrow> borrowedBooks = borrowRepository.findAll();
        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("admin/get-borrowed-books-history")
    public ResponseEntity<List<Borrow>> getAdminBorrowedBooksHistory() {
        List<Borrow> borrowedBooks = borrowRepository.findAll();
        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("librarian/get-issued-books")
    public ResponseEntity<List<Borrow>> getIssuedBooks() {
        List<Borrow> issuedBooks = borrowRepository.findByReturnDateIsNull();
        return ResponseEntity.ok(issuedBooks);
    }

    @GetMapping("admin/get-issued-books")
    public ResponseEntity<List<Borrow>> getAdminBorrowedBooks() {
        List<Borrow> borrowedBooks = borrowRepository.findByReturnDateIsNull();
        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("admin/get-available-books")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> availableBooks = bookRepository.findByNumBooksGreaterThan(0);
        return ResponseEntity.ok(availableBooks);
    }

    @GetMapping("librarian/user-record/{email}")
    public ResponseEntity<List<Borrow>> getUserRecord(@PathVariable String email) {
        List<Borrow> userRecord = borrowRepository.findByEmail(email);
        return ResponseEntity.ok(userRecord);
    }

    @GetMapping("librarian/get-overdue-books")
    public ResponseEntity<List<Borrow>> getOverdueBooks() {
        List<Borrow> overdueBooks = borrowRepository.findByReturnDateIsNullAndDueDateBefore(new Date());
        return ResponseEntity.ok(overdueBooks);
    }

    @GetMapping("alluser/notification")
    public ResponseEntity<List<Borrow>> getNotification() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        switch (user.getRole()) {
            case "LIBRARIAN" -> {
                List<Borrow> overdueBooks = borrowRepository.findByReturnDateIsNullAndDueDateBefore(new Date());
                return ResponseEntity.ok(overdueBooks);
            }
            case "USER" -> {
                List<Borrow> overdueBooks = borrowRepository.findByReturnDateIsNullAndDueDateBeforeAndEmail(new Date(), email);
                return ResponseEntity.ok(overdueBooks);
            }
            default -> {
                return ResponseEntity.status(400).body(null);
            }
        }
    }
}
