package com.tapestria.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @Column(unique = true)
    String isbn;
    String title;
    String author;
    @Column(name = "bookformat")
    String bookFormat;
    @Column(name = "description")
    String description;
    @Column(name = "img")
    String imageLink;
    @Column(precision = 3, scale = 2)
    BigDecimal rating;
    @Column(name = "totalratings")
    Integer numRatings;
    @Column(name = "genre")
    String genres;
    @Column(name = "numcopies")
    Integer numBooks;

    public void borrowBook() {
        this.numBooks--;
    }

    public void returnBook() {
        this.numBooks++;
    }

    public boolean isAvailable() {
        return this.numBooks > 0;
    }

    public void addRating(Double newRating) {
        BigDecimal newRatingBD = BigDecimal.valueOf(newRating).setScale(2, RoundingMode.HALF_UP);

        if (this.numRatings == 0) this.rating = newRatingBD;
        else {
            BigDecimal total = this.rating.multiply(BigDecimal.valueOf(this.numRatings))
                                        .add(newRatingBD);
            this.rating = total.divide(BigDecimal.valueOf(this.numRatings + 1), 2, RoundingMode.HALF_UP);
        }
        this.numRatings++;
    }
}
