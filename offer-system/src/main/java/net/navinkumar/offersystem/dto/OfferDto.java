package net.navinkumar.offersystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.navinkumar.offersystem.entity.Offer;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    
    private Long id;
    
    @NotBlank(message = "Offer code is required")
    @Size(max = 20, message = "Offer code must not exceed 20 characters")
    private String code;
    
    @NotBlank(message = "Offer name is required")
    @Size(max = 100, message = "Offer name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Property ID is required")
    private String propertyId;
    
    @NotNull(message = "Offer type is required")
    private Offer.OfferType offerType;
    
    @NotNull(message = "Discount type is required")
    private Offer.DiscountType discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Discount value must have at most 8 integer digits and 2 decimal places")
    private BigDecimal discountValue;
    
    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;
    
    private LocalDate validTo;
    
    private Boolean isActive = true;
    
    private Boolean requiresApproval = false;
    
    @NotBlank(message = "Authorized department is required")
    private String authorizedDepartment;
    
    @Min(value = 0, message = "Minimum advance booking must be non-negative")
    private Integer minimumAdvanceBooking;
    
    @Min(value = 0, message = "Maximum advance booking must be non-negative")
    private Integer maximumAdvanceBooking;
    
    @Min(value = 1, message = "Minimum stay must be at least 1")
    private Integer minimumStay;
    
    @Min(value = 1, message = "Maximum stay must be at least 1")
    private Integer maximumStay;
    
    private String applicableRoomTypes;
    
    private String applicableRatePlans;
    
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;
}