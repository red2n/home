package net.navinkumar.offersystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferEligibilityRequest {
    
    @NotBlank(message = "Property ID is required")
    private String propertyId;
    
    @NotBlank(message = "Room type is required")
    private String roomType;
    
    @NotBlank(message = "Rate plan code is required")
    private String ratePlanCode;
    
    @NotNull(message = "Base amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base amount must be greater than 0")
    private BigDecimal baseAmount;
    
    private String guestId;
    
    @Min(value = 1, message = "Stay duration must be at least 1")
    private Integer stayDuration;
    
    @Min(value = 0, message = "Advance booking days must be non-negative")
    private Integer advanceBookingDays;
    
    private String department;
}