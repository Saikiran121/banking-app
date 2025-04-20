package com.banking.app.repository;

import com.banking.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountIdInOrToAccountNumberIn(List<Long> accountIds, List<String> accountNumbers);
}
