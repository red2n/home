package net.navinkumar.ledger.repository;

import net.navinkumar.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    
    List<LedgerEntry> findByAccountId(Long accountId);
    
    List<LedgerEntry> findByJournalEntryId(Long journalEntryId);
    
    @Query("SELECT l FROM LedgerEntry l WHERE l.account.id = :accountId AND l.createdAt BETWEEN :startDate AND :endDate")
    List<LedgerEntry> findByAccountAndDateRange(@Param("accountId") Long accountId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}