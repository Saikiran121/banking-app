package com.banking.app.service;

import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void transfer(Long fromAccountId, String toAccountNumber, Double amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountNumber(toAccountNumber);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void deposit(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setBalance(account.getBalance() + amount);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(null); // No source account for deposits
        transaction.setToAccountNumber(account.getAccountNumber());
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        List<Long> accountIds = userAccounts.stream().map(Account::getId).toList();
        return transactionRepository.findByFromAccountIdInOrToAccountNumberIn(
                accountIds, 
                userAccounts.stream().map(Account::getAccountNumber).toList()
        );
    }
}
