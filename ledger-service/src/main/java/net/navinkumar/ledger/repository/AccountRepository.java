package net.navinkumar.ledger.repository;

import net.navinkumar.ledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByAccountType(Account.AccountType accountType);
    
    List<Account> findByIsActiveTrue();
    
    Optional<Account> findByAccountNameIgnoreCase(String accountName);
}