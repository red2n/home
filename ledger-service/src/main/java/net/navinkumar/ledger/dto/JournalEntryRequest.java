package net.navinkumar.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryRequest {
    
    @NotNull(message = "Description is required")
    private String description;
    
    private LocalDateTime entryDate;
    
    private String reference;
    
    @NotNull(message = "Ledger entries are required")
    private List<LedgerEntryRequest> ledgerEntries;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LedgerEntryRequest {
        
        @NotNull(message = "Account ID is required")
        private Long accountId;
        
        @NotNull(message = "Description is required")
        private String description;
        
        private BigDecimal debitAmount = BigDecimal.ZERO;
        
        private BigDecimal creditAmount = BigDecimal.ZERO;
    }
}