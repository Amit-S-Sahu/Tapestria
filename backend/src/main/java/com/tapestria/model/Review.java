package com.tapestria.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer reviewId;
    String isbn;
    String email;
    @Column(precision = 3, scale = 2)
    BigDecimal rating;
    String reviewText;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JSONDataSerializer.class)
    Date reviewDate;
}
