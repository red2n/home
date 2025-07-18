package net.navinkumar.ledger.controller;

import lombok.RequiredArgsConstructor;
import net.navinkumar.ledger.dto.AccountRequest;
import net.navinkumar.ledger.dto.JournalEntryRequest;
import net.navinkumar.ledger.entity.Account;
import net.navinkumar.ledger.entity.JournalEntry;
import net.navinkumar.ledger.entity.LedgerEntry;
import net.navinkumar.ledger.service.LedgerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Validated
public class LedgerController {
    
    private final LedgerService ledgerService;
    
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest request) {
        Account account = ledgerService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @PostMapping("/journal-entries")
    public ResponseEntity<JournalEntry> createJournalEntry(@Valid @RequestBody JournalEntryRequest request) {
        JournalEntry journalEntry = ledgerService.createJournalEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(journalEntry);
    }
    
    @PutMapping("/journal-entries/{id}/post")
    public ResponseEntity<JournalEntry> postJournalEntry(@PathVariable Long id) {
        JournalEntry journalEntry = ledgerService.postJournalEntry(id);
        return ResponseEntity.ok(journalEntry);
    }
    
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return ledgerService.getAccountById(id)
            .map(account -> ResponseEntity.ok(account))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/accounts/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        return ledgerService.getAccountByNumber(accountNumber)
            .map(account -> ResponseEntity.ok(account))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllActiveAccounts() {
        List<Account> accounts = ledgerService.getAllActiveAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/accounts/type/{accountType}")
    public ResponseEntity<List<Account>> getAccountsByType(@PathVariable String accountType) {
        Account.AccountType type = Account.AccountType.valueOf(accountType);
        List<Account> accounts = ledgerService.getAccountsByType(type);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/journal-entries/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable Long id) {
        return ledgerService.getJournalEntryById(id)
            .map(journalEntry -> ResponseEntity.ok(journalEntry))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/accounts/{accountId}/entries")
    public ResponseEntity<List<LedgerEntry>> getLedgerEntriesByAccountId(@PathVariable Long accountId) {
        List<LedgerEntry> entries = ledgerService.getLedgerEntriesByAccountId(accountId);
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountId) {
        BigDecimal balance = ledgerService.getAccountBalance(accountId);
        return ResponseEntity.ok(balance);
    }
}