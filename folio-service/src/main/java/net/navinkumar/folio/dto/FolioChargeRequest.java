package net.navinkumar.folio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolioChargeRequest {
    
    @NotNull(message = "Folio ID is required")
    private Long folioId;
    
    @NotNull(message = "Description is required")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Charge type is required")
    private String chargeType;
    
    private LocalDateTime chargeDate;
    
    private String reference;
}