package net.navinkumar.ratemanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatePlanDto {
    
    private Long id;
    
    @NotBlank(message = "Rate plan code is required")
    @Size(max = 20, message = "Rate plan code must not exceed 20 characters")
    private String code;
    
    @NotBlank(message = "Rate plan name is required")
    @Size(max = 100, message = "Rate plan name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Property ID is required")
    private String propertyId;
    
    @NotBlank(message = "Room type is required")
    private String roomType;
    
    @NotNull(message = "Base rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base rate must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Base rate must have at most 8 integer digits and 2 decimal places")
    private BigDecimal baseRate;
    
    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    private String currencyCode;
    
    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;
    
    private LocalDate validTo;
    
    private Boolean isActive = true;
    
    private Boolean isRefundable = true;
    
    @Min(value = 0, message = "Advance booking days must be non-negative")
    private Integer advanceBookingDays;
    
    @Min(value = 1, message = "Minimum stay must be at least 1")
    private Integer minimumStay = 1;
    
    @Min(value = 1, message = "Maximum stay must be at least 1")
    private Integer maximumStay;
}