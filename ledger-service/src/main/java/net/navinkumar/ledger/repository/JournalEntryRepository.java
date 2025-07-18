package net.navinkumar.ledger.repository;

import net.navinkumar.ledger.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    
    Optional<JournalEntry> findByEntryNumber(String entryNumber);
    
    List<JournalEntry> findByStatus(JournalEntry.EntryStatus status);
    
    @Query("SELECT j FROM JournalEntry j WHERE j.entryDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findByEntryDateBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
}