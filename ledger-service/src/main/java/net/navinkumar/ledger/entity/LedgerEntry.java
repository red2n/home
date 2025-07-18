package net.navinkumar.ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false)
    private String description;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private String createdBy;
}