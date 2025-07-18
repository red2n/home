package net.navinkumar.ledger.service;

import net.navinkumar.ledger.dto.AccountRequest;
import net.navinkumar.ledger.entity.Account;
import net.navinkumar.ledger.repository.AccountRepository;
import net.navinkumar.ledger.repository.JournalEntryRepository;
import net.navinkumar.ledger.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private LedgerService ledgerService;

    @Test
    void createAccount_ShouldReturnCreatedAccount() {
        // Given
        AccountRequest request = new AccountRequest();
        request.setAccountName("Cash Account");
        request.setAccountType("ASSET");
        request.setDescription("Main cash account");

        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setAccountName("Cash Account");
        savedAccount.setAccountType(Account.AccountType.ASSET);
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByAccountNameIgnoreCase("Cash Account")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // When
        Account result = ledgerService.createAccount(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cash Account", result.getAccountName());
        assertEquals(Account.AccountType.ASSET, result.getAccountType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void createAccount_WithDuplicateName_ShouldThrowException() {
        // Given
        AccountRequest request = new AccountRequest();
        request.setAccountName("Cash Account");
        request.setAccountType("ASSET");

        Account existingAccount = new Account();
        existingAccount.setAccountName("Cash Account");

        when(accountRepository.findByAccountNameIgnoreCase("Cash Account")).thenReturn(Optional.of(existingAccount));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ledgerService.createAccount(request);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void getAccountBalance_ShouldReturnCorrectBalance() {
        // Given
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When
        BigDecimal balance = ledgerService.getAccountBalance(1L);

        // Then
        assertEquals(new BigDecimal("1000.00"), balance);
    }
}