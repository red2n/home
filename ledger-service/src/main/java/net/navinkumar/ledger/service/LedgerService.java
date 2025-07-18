package net.navinkumar.ledger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.navinkumar.ledger.dto.AccountRequest;
import net.navinkumar.ledger.dto.JournalEntryRequest;
import net.navinkumar.ledger.entity.Account;
import net.navinkumar.ledger.entity.JournalEntry;
import net.navinkumar.ledger.entity.LedgerEntry;
import net.navinkumar.ledger.repository.AccountRepository;
import net.navinkumar.ledger.repository.JournalEntryRepository;
import net.navinkumar.ledger.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LedgerService {
    
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    
    public Account createAccount(AccountRequest request) {
        log.info("Creating account: {}", request.getAccountName());
        
        // Check if account name already exists
        Optional<Account> existingAccount = accountRepository.findByAccountNameIgnoreCase(request.getAccountName());
        if (existingAccount.isPresent()) {
            throw new RuntimeException("Account with name '" + request.getAccountName() + "' already exists");
        }
        
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountName(request.getAccountName());
        account.setAccountType(Account.AccountType.valueOf(request.getAccountType()));
        account.setDescription(request.getDescription());
        account.setCreatedBy("system");
        
        return accountRepository.save(account);
    }
    
    public JournalEntry createJournalEntry(JournalEntryRequest request) {
        log.info("Creating journal entry: {}", request.getDescription());
        
        // Validate that debits equal credits
        BigDecimal totalDebits = request.getLedgerEntries().stream()
            .map(entry -> entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCredits = request.getLedgerEntries().stream()
            .map(entry -> entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new RuntimeException("Total debits must equal total credits");
        }
        
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setEntryNumber(generateJournalEntryNumber());
        journalEntry.setEntryDate(request.getEntryDate() != null ? request.getEntryDate() : LocalDateTime.now());
        journalEntry.setDescription(request.getDescription());
        journalEntry.setTotalAmount(totalDebits); // or totalCredits, they're equal
        journalEntry.setReference(request.getReference());
        journalEntry.setStatus(JournalEntry.EntryStatus.PENDING);
        journalEntry.setCreatedBy("system");
        
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);
        
        // Create ledger entries
        for (JournalEntryRequest.LedgerEntryRequest ledgerEntryRequest : request.getLedgerEntries()) {
            Account account = accountRepository.findById(ledgerEntryRequest.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + ledgerEntryRequest.getAccountId()));
                
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.setJournalEntry(savedJournalEntry);
            ledgerEntry.setAccount(account);
            ledgerEntry.setDescription(ledgerEntryRequest.getDescription());
            ledgerEntry.setDebitAmount(ledgerEntryRequest.getDebitAmount());
            ledgerEntry.setCreditAmount(ledgerEntryRequest.getCreditAmount());
            ledgerEntry.setCreatedBy("system");
            
            ledgerEntryRepository.save(ledgerEntry);
        }
        
        return savedJournalEntry;
    }
    
    public JournalEntry postJournalEntry(Long journalEntryId) {
        log.info("Posting journal entry: {}", journalEntryId);
        
        JournalEntry journalEntry = journalEntryRepository.findById(journalEntryId)
            .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + journalEntryId));
            
        if (journalEntry.getStatus() != JournalEntry.EntryStatus.PENDING) {
            throw new RuntimeException("Only pending journal entries can be posted");
        }
        
        // Update account balances
        List<LedgerEntry> ledgerEntries = ledgerEntryRepository.findByJournalEntryId(journalEntryId);
        for (LedgerEntry ledgerEntry : ledgerEntries) {
            Account account = ledgerEntry.getAccount();
            
            // Update totals
            account.setDebitTotal(account.getDebitTotal().add(ledgerEntry.getDebitAmount()));
            account.setCreditTotal(account.getCreditTotal().add(ledgerEntry.getCreditAmount()));
            
            // Update balance based on account type
            BigDecimal netChange = ledgerEntry.getDebitAmount().subtract(ledgerEntry.getCreditAmount());
            if (account.getAccountType() == Account.AccountType.ASSET || 
                account.getAccountType() == Account.AccountType.EXPENSE) {
                // For assets and expenses, debits increase balance
                account.setBalance(account.getBalance().add(netChange));
            } else {
                // For liabilities, equity, and revenue, credits increase balance
                account.setBalance(account.getBalance().subtract(netChange));
            }
            
            account.setUpdatedBy("system");
            accountRepository.save(account);
        }
        
        journalEntry.setStatus(JournalEntry.EntryStatus.POSTED);
        return journalEntryRepository.save(journalEntry);
    }
    
    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Account> getAllActiveAccounts() {
        return accountRepository.findByIsActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Account> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }
    
    @Transactional(readOnly = true)
    public Optional<JournalEntry> getJournalEntryById(Long id) {
        return journalEntryRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<LedgerEntry> getLedgerEntriesByAccountId(Long accountId) {
        return ledgerEntryRepository.findByAccountId(accountId);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        return account.getBalance();
    }
    
    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateJournalEntryNumber() {
        return "JE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}