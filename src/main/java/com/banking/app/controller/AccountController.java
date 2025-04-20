package com.banking.app.controller;

import com.banking.app.entity.Account;
import com.banking.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long userId, @RequestParam String accountType) {
        Account account = accountService.createAccount(userId, accountType);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }
}
