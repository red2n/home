package net.navinkumar.ratemanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateCalculationResponse {
    
    private String ratePlanCode;
    private String ratePlanName;
    private BigDecimal totalAmount;
    private String currencyCode;
    private Integer numberOfNights;
    private BigDecimal averageNightlyRate;
    private List<DailyRate> dailyRates;
    private Boolean isRefundable;
    private Integer minimumStay;
    private Integer maximumStay;
    private List<String> restrictions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRate {
        private LocalDate date;
        private BigDecimal rate;
        private String appliedSeason;
        private Boolean hasRestrictions;
    }
}