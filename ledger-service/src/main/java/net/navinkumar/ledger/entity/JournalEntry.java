package net.navinkumar.ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String entryNumber;
    
    @Column(nullable = false)
    private LocalDateTime entryDate;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Column
    private String reference;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;
    
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LedgerEntry> ledgerEntries = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    public enum EntryStatus {
        PENDING, POSTED, REVERSED
    }
}