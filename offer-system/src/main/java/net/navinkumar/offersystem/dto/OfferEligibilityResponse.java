package net.navinkumar.offersystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferEligibilityResponse {
    
    private List<EligibleOffer> eligibleOffers;
    private BigDecimal originalAmount;
    private BigDecimal bestDiscountAmount;
    private BigDecimal bestFinalAmount;
    private String recommendedOfferCode;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EligibleOffer {
        private String offerCode;
        private String offerName;
        private String description;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private Boolean requiresApproval;
        private String authorizedDepartment;
        private String discountType;
        private BigDecimal discountValue;
    }
}