package net.navinkumar.folio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolioRequest {
    
    @NotNull(message = "Guest ID is required")
    private Long guestId;
    
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;
    
    private BigDecimal initialAmount = BigDecimal.ZERO;
}