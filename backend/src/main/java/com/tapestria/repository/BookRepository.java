package com.tapestria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tapestria.model.Book;

public interface BookRepository extends JpaRepository<Book, String> {
    Optional<Book> findByTitle(String title);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByGenresContaining(String genre);
}
