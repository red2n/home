package net.navinkumar.offersystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferApprovalRequest {
    
    @NotNull(message = "Offer ID is required")
    private Long offerId;
    
    private String reservationId;
    
    private String guestId;
    
    @Size(max = 500, message = "Comments must not exceed 500 characters")
    private String comments;
}