package net.navinkumar.ratemanagement.service;

import net.navinkumar.ratemanagement.dto.RateCalculationRequest;
import net.navinkumar.ratemanagement.dto.RateCalculationResponse;
import net.navinkumar.ratemanagement.dto.RatePlanDto;
import net.navinkumar.ratemanagement.entity.RatePlan;
import net.navinkumar.ratemanagement.entity.SeasonalRate;
import net.navinkumar.ratemanagement.entity.RateRestriction;
import net.navinkumar.ratemanagement.repository.RatePlanRepository;
import net.navinkumar.ratemanagement.repository.SeasonalRateRepository;
import net.navinkumar.ratemanagement.repository.RateRestrictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RateManagementService {
    
    private final RatePlanRepository ratePlanRepository;
    private final SeasonalRateRepository seasonalRateRepository;
    private final RateRestrictionRepository rateRestrictionRepository;
    
    public RatePlan createRatePlan(RatePlanDto ratePlanDto, String userId) {
        log.info("Creating rate plan with code: {}", ratePlanDto.getCode());
        
        // Check if rate plan code already exists
        if (ratePlanRepository.findByCode(ratePlanDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Rate plan with code " + ratePlanDto.getCode() + " already exists");
        }
        
        RatePlan ratePlan = convertToEntity(ratePlanDto);
        ratePlan.setCreatedBy(userId);
        ratePlan.setUpdatedBy(userId);
        
        return ratePlanRepository.save(ratePlan);
    }
    
    public RatePlan updateRatePlan(Long id, RatePlanDto ratePlanDto, String userId) {
        log.info("Updating rate plan with id: {}", id);
        
        RatePlan existingRatePlan = ratePlanRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Rate plan not found with id: " + id));
        
        // Check if code is being changed and if new code already exists
        if (!existingRatePlan.getCode().equals(ratePlanDto.getCode()) &&
            ratePlanRepository.findByCode(ratePlanDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Rate plan with code " + ratePlanDto.getCode() + " already exists");
        }
        
        updateEntityFromDto(existingRatePlan, ratePlanDto);
        existingRatePlan.setUpdatedBy(userId);
        
        return ratePlanRepository.save(existingRatePlan);
    }
    
    @Transactional(readOnly = true)
    public List<RatePlan> getActiveRatePlansForProperty(String propertyId) {
        return ratePlanRepository.findByPropertyIdAndIsActiveTrue(propertyId);
    }
    
    @Transactional(readOnly = true)
    public RateCalculationResponse calculateRate(RateCalculationRequest request) {
        log.info("Calculating rate for property: {}, room type: {}, dates: {} to {}",
                request.getPropertyId(), request.getRoomType(), request.getCheckInDate(), request.getCheckOutDate());
        
        // Find applicable rate plan
        RatePlan ratePlan = findApplicableRatePlan(request);
        if (ratePlan == null) {
            throw new IllegalArgumentException("No valid rate plan found for the given criteria");
        }
        
        // Validate stay period
        validateStayPeriod(request, ratePlan);
        
        // Calculate daily rates
        List<RateCalculationResponse.DailyRate> dailyRates = calculateDailyRates(ratePlan, request);
        
        // Calculate total amount
        BigDecimal totalAmount = dailyRates.stream()
            .map(RateCalculationResponse.DailyRate::getRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate average nightly rate
        int numberOfNights = dailyRates.size();
        BigDecimal averageNightlyRate = totalAmount.divide(BigDecimal.valueOf(numberOfNights), 2, RoundingMode.HALF_UP);
        
        // Check for restrictions
        List<String> restrictions = checkRestrictions(ratePlan, request);
        
        return new RateCalculationResponse(
            ratePlan.getCode(),
            ratePlan.getName(),
            totalAmount,
            ratePlan.getCurrencyCode(),
            numberOfNights,
            averageNightlyRate,
            dailyRates,
            ratePlan.getIsRefundable(),
            ratePlan.getMinimumStay(),
            ratePlan.getMaximumStay(),
            restrictions
        );
    }
    
    private RatePlan findApplicableRatePlan(RateCalculationRequest request) {
        List<RatePlan> ratePlans;
        
        if (request.getRatePlanCode() != null && !request.getRatePlanCode().isEmpty()) {
            Optional<RatePlan> ratePlan = ratePlanRepository.findByCode(request.getRatePlanCode());
            if (ratePlan.isPresent() && isRatePlanValidForDates(ratePlan.get(), request.getCheckInDate())) {
                return ratePlan.get();
            }
            return null;
        } else {
            ratePlans = ratePlanRepository.findValidRatePlansForDate(
                request.getPropertyId(), request.getRoomType(), request.getCheckInDate());
        }
        
        // Return the first valid rate plan (could implement BAR logic here)
        return ratePlans.isEmpty() ? null : ratePlans.get(0);
    }
    
    private boolean isRatePlanValidForDates(RatePlan ratePlan, LocalDate checkDate) {
        return ratePlan.getValidFrom().compareTo(checkDate) <= 0 &&
               (ratePlan.getValidTo() == null || ratePlan.getValidTo().compareTo(checkDate) >= 0);
    }
    
    private void validateStayPeriod(RateCalculationRequest request, RatePlan ratePlan) {
        int stayDuration = Period.between(request.getCheckInDate(), request.getCheckOutDate()).getDays();
        
        if (stayDuration < ratePlan.getMinimumStay()) {
            throw new IllegalArgumentException("Stay duration is less than minimum stay requirement of " + ratePlan.getMinimumStay() + " nights");
        }
        
        if (ratePlan.getMaximumStay() != null && stayDuration > ratePlan.getMaximumStay()) {
            throw new IllegalArgumentException("Stay duration exceeds maximum stay limit of " + ratePlan.getMaximumStay() + " nights");
        }
    }
    
    private List<RateCalculationResponse.DailyRate> calculateDailyRates(RatePlan ratePlan, RateCalculationRequest request) {
        List<RateCalculationResponse.DailyRate> dailyRates = new ArrayList<>();
        LocalDate currentDate = request.getCheckInDate();
        
        while (currentDate.isBefore(request.getCheckOutDate())) {
            BigDecimal dailyRate = calculateDailyRate(ratePlan, currentDate);
            String appliedSeason = getAppliedSeasonName(ratePlan, currentDate);
            Boolean hasRestrictions = hasRestrictionsForDate(ratePlan, currentDate);
            
            dailyRates.add(new RateCalculationResponse.DailyRate(
                currentDate, dailyRate, appliedSeason, hasRestrictions));
            
            currentDate = currentDate.plusDays(1);
        }
        
        return dailyRates;
    }
    
    private BigDecimal calculateDailyRate(RatePlan ratePlan, LocalDate date) {
        BigDecimal baseRate = ratePlan.getBaseRate();
        
        // Apply seasonal adjustments
        List<SeasonalRate> applicableSeasonalRates = seasonalRateRepository
            .findApplicableSeasonalRates(ratePlan.getId(), date);
        
        for (SeasonalRate seasonalRate : applicableSeasonalRates) {
            if (seasonalRate.getAdjustmentType() == SeasonalRate.AdjustmentType.PERCENTAGE) {
                baseRate = baseRate.multiply(BigDecimal.ONE.add(seasonalRate.getRateAdjustment().divide(BigDecimal.valueOf(100))));
            } else {
                baseRate = baseRate.add(seasonalRate.getRateAdjustment());
            }
        }
        
        return baseRate.setScale(2, RoundingMode.HALF_UP);
    }
    
    private String getAppliedSeasonName(RatePlan ratePlan, LocalDate date) {
        List<SeasonalRate> applicableSeasonalRates = seasonalRateRepository
            .findApplicableSeasonalRates(ratePlan.getId(), date);
        
        return applicableSeasonalRates.isEmpty() ? "Base Rate" : applicableSeasonalRates.get(0).getName();
    }
    
    private Boolean hasRestrictionsForDate(RatePlan ratePlan, LocalDate date) {
        Optional<RateRestriction> restriction = rateRestrictionRepository
            .findByRatePlanIdAndRestrictionDate(ratePlan.getId(), date);
        
        return restriction.isPresent();
    }
    
    private List<String> checkRestrictions(RatePlan ratePlan, RateCalculationRequest request) {
        List<String> restrictions = new ArrayList<>();
        
        // Check arrival restrictions
        Optional<RateRestriction> arrivalRestriction = rateRestrictionRepository
            .findByRatePlanIdAndRestrictionDate(ratePlan.getId(), request.getCheckInDate());
        
        if (arrivalRestriction.isPresent()) {
            RateRestriction restriction = arrivalRestriction.get();
            if (Boolean.TRUE.equals(restriction.getIsClosedToArrival())) {
                restrictions.add("Closed to arrival on " + request.getCheckInDate());
            }
            if (Boolean.TRUE.equals(restriction.getStopSell())) {
                restrictions.add("Stop sell restriction on " + request.getCheckInDate());
            }
        }
        
        return restrictions;
    }
    
    private RatePlan convertToEntity(RatePlanDto dto) {
        RatePlan entity = new RatePlan();
        updateEntityFromDto(entity, dto);
        return entity;
    }
    
    private void updateEntityFromDto(RatePlan entity, RatePlanDto dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPropertyId(dto.getPropertyId());
        entity.setRoomType(dto.getRoomType());
        entity.setBaseRate(dto.getBaseRate());
        entity.setCurrencyCode(dto.getCurrencyCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setIsActive(dto.getIsActive());
        entity.setIsRefundable(dto.getIsRefundable());
        entity.setAdvanceBookingDays(dto.getAdvanceBookingDays());
        entity.setMinimumStay(dto.getMinimumStay());
        entity.setMaximumStay(dto.getMaximumStay());
    }
}