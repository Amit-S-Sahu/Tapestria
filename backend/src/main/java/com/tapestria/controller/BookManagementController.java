package com.tapestria.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tapestria.model.Book;
import com.tapestria.model.Review;
import com.tapestria.model.User;
import com.tapestria.repository.BookRepository;
import com.tapestria.repository.ReviewRepository;
import com.tapestria.repository.UserRepository;

@RestController
public class BookManagementController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/alluser/get-all-books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/alluser/check-availability/{bookId}")
    public ResponseEntity<String> checkAvailability(@PathVariable String bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        return ResponseEntity.ok(book.isAvailable() ? "Available" : "Not Available");
    }

    @GetMapping("/alluser/get-book-by-id/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable String bookId) {
        return ResponseEntity.ok(bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found")));
    }

    @GetMapping("/alluser/get-book-by-author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(bookRepository.findByAuthorContaining(authorId));
    }

    @GetMapping("/alluser/get-book-by-genre/{genreId}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genreId) {
        return ResponseEntity.ok(bookRepository.findByGenresContaining(genreId));
    }

    @GetMapping("/alluser/get-book-by-title/{title}")
    public ResponseEntity<Book> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookRepository.findByTitle(title).orElseThrow(() -> new RuntimeException("Book with title " + title + " not found")));
    }

    @PostMapping("/librarian/add-book")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PostMapping("/librarian/update-book/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable String bookId, @RequestBody Book book) {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setBookFormat(book.getBookFormat());
        existingBook.setDescription(book.getDescription());
        existingBook.setImageLink(book.getImageLink());
        existingBook.setRating(book.getRating());
        existingBook.setNumRatings(book.getNumRatings());
        existingBook.setGenres(book.getGenres());
        return ResponseEntity.ok(bookRepository.save(existingBook));
    }

    @DeleteMapping("/librarian/delete-book/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable String bookId) {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        bookRepository.delete(existingBook);
        return ResponseEntity.ok("Book with id " + bookId + " deleted successfully");
    }

    @PostMapping("/user/add-rating/{bookId}/{rating}")
    public ResponseEntity<Book> addRating(@PathVariable String bookId, @PathVariable Integer rating) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        existingBook.addRating(Double.valueOf(rating));

        Review review = new Review();
        review.setIsbn(bookId);
        review.setEmail(email);
        review.setRating(BigDecimal.valueOf(rating));
        review.setReviewDate(new Date());

        reviewRepository.save(review);
        return ResponseEntity.ok(bookRepository.save(existingBook));
    }

    @PostMapping("/user/add-review/{bookId}")
    public ResponseEntity<Review> addReview(@PathVariable String bookId, @RequestBody Review review) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        review.setIsbn(bookId);
        review.setEmail(email);
        review.setReviewDate(new Date());
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @GetMapping("/alluser/get-review-by-book/{bookId}")
    public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable String bookId) {
        return ResponseEntity.ok(reviewRepository.findByIsbn(bookId));
    }

    @GetMapping("/alluser/get-review-by-user/{email}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable String email) {
        return ResponseEntity.ok(reviewRepository.findByEmail(email));
    }

    @GetMapping("/alluser/get-review-by-rating/{minRating}/{maxRating}")
    public ResponseEntity<List<Review>> getReviewsByRating(@PathVariable BigDecimal minRating, @PathVariable BigDecimal maxRating) {
        return ResponseEntity.ok(reviewRepository.findByRatingBetween(minRating, maxRating));
    }

    @DeleteMapping("/user/delete-review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review with id " + reviewId + " not found"));
        reviewRepository.delete(existingReview);
        return ResponseEntity.ok("Review with id " + reviewId + " deleted successfully");
    }

    @DeleteMapping("/librarian/delete-review/{reviewId}")
    public ResponseEntity<String> deleteReviewLibrarian(@PathVariable Integer reviewId) {
        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review with id " + reviewId + " not found"));
        reviewRepository.delete(existingReview);
        return ResponseEntity.ok("Review with id " + reviewId + " deleted successfully");
    }
}