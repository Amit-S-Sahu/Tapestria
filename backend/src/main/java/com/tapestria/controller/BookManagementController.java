package com.tapestria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tapestria.model.Book;
import com.tapestria.repository.BookRepository;

@RestController
public class BookManagementController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/alluser/get-all-books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/alluser/get-book-by-id/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable String bookId) {
        return ResponseEntity.ok(bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found")));
    }

    @GetMapping("/alluser/get-book-by-author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(bookRepository.findByAuthorContaining(authorId));
    }

    @GetMapping("/alluser/get-book-by-genre/{genreId}") //! Fix genre segmentation by ,
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

    @PutMapping("/user/add-rating/{bookId}/{rating}")
    public ResponseEntity<Book> addRating(@PathVariable String bookId, @PathVariable Integer rating) {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
        existingBook.addRating(Double.valueOf(rating));
        return ResponseEntity.ok(bookRepository.save(existingBook));
    }

    // !Update after adding review functionality
    // @PutMapping("/user/update-rating/{bookId}/{rating}")
    // public ResponseEntity<Book> updateRating(@PathVariable String bookId, @PathVariable Integer rating) {
    //     Book existingBook = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book with id " + bookId + " not found"));
    //     existingBook.setRating(existingBook.getRating().multiply(BigDecimal.valueOf(existingBook.getNumRatings())).add(BigDecimal.valueOf(rating)).divide(BigDecimal.valueOf(existingBook.getNumRatings() + 1), 2, BigDecimal.ROUND_HALF_UP));
    //     return ResponseEntity.ok(bookRepository.save(existingBook));
    // }
}
