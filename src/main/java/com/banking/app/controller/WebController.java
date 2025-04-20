package com.banking.app.controller;

import com.banking.app.repository.UserRepository;
import com.banking.app.service.AccountService;
import com.banking.app.service.TransactionService;
import com.banking.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String fullName,
            Model model) {
        try {
            userService.registerUser(username, password, email, fullName);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : "Guest";
        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/accounts")
    public String accounts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        Long userId = userRepository.findByUsername(username)
                .map(com.banking.app.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("accounts", accountService.getAccountsByUserId(userId));
        return "accounts";
    }

    @GetMapping("/add-account")
    public String addAccountForm() {
        return "add-account";
    }

    @PostMapping("/add-account")
    public String addAccount(@RequestParam String accountType, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        Long userId = userRepository.findByUsername(username)
                .map(com.banking.app.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        try {
            accountService.createAccount(userId, accountType);
            return "redirect:/accounts";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "add-account";
        }
    }

    @GetMapping("/transfer")
    public String transferForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        Long userId = userRepository.findByUsername(username)
                .map(com.banking.app.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("accounts", accountService.getAccountsByUserId(userId));
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccountId, @RequestParam String toAccountNumber, @RequestParam Double amount) {
        transactionService.transfer(fromAccountId, toAccountNumber, amount);
        return "redirect:/dashboard";
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        Long userId = userRepository.findByUsername(username)
                .map(com.banking.app.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("transactions", transactionService.getTransactionsByUserId(userId));
        return "transactions";
    }

    @GetMapping("/deposit")
    public String depositForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated() ? auth.getName() : null;
        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }
        Long userId = userRepository.findByUsername(username)
                .map(com.banking.app.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("accounts", accountService.getAccountsByUserId(userId));
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountId, @RequestParam Double amount, Model model) {
        try {
            transactionService.deposit(accountId, amount);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Long userId = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .map(com.banking.app.entity.User::getId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            model.addAttribute("accounts", accountService.getAccountsByUserId(userId));
            return "deposit";
        }
    }
}
