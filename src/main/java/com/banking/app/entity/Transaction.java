package com.banking.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_number", nullable = false)
    private String toAccountNumber;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
