package com.tapestria.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "borrows")
@Data
public class Borrow {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Integer borrowId;
    String isbn;
    String email;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JSONDataSerializer.class)
    Date issueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JSONDataSerializer.class)
    Date returnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JSONDataSerializer.class)
    Date dueDate;

    private BigDecimal fineAmount;
}
