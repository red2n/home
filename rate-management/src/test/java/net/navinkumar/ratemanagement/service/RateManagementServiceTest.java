package net.navinkumar.ratemanagement.service;

import net.navinkumar.ratemanagement.dto.RateCalculationRequest;
import net.navinkumar.ratemanagement.dto.RateCalculationResponse;
import net.navinkumar.ratemanagement.dto.RatePlanDto;
import net.navinkumar.ratemanagement.entity.RatePlan;
import net.navinkumar.ratemanagement.repository.RatePlanRepository;
import net.navinkumar.ratemanagement.repository.SeasonalRateRepository;
import net.navinkumar.ratemanagement.repository.RateRestrictionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateManagementServiceTest {

    @Mock
    private RatePlanRepository ratePlanRepository;

    @Mock
    private SeasonalRateRepository seasonalRateRepository;

    @Mock
    private RateRestrictionRepository rateRestrictionRepository;

    @InjectMocks
    private RateManagementService rateManagementService;

    private RatePlanDto ratePlanDto;
    private RatePlan ratePlan;

    @BeforeEach
    void setUp() {
        ratePlanDto = new RatePlanDto();
        ratePlanDto.setCode("STANDARD");
        ratePlanDto.setName("Standard Rate");
        ratePlanDto.setPropertyId("PROP001");
        ratePlanDto.setRoomType("DELUXE");
        ratePlanDto.setBaseRate(new BigDecimal("100.00"));
        ratePlanDto.setCurrencyCode("USD");
        ratePlanDto.setValidFrom(LocalDate.now());
        ratePlanDto.setMinimumStay(1);
        ratePlanDto.setIsActive(true);
        ratePlanDto.setIsRefundable(true);

        ratePlan = new RatePlan();
        ratePlan.setId(1L);
        ratePlan.setCode("STANDARD");
        ratePlan.setName("Standard Rate");
        ratePlan.setPropertyId("PROP001");
        ratePlan.setRoomType("DELUXE");
        ratePlan.setBaseRate(new BigDecimal("100.00"));
        ratePlan.setCurrencyCode("USD");
        ratePlan.setValidFrom(LocalDate.now());
        ratePlan.setMinimumStay(1);
        ratePlan.setIsActive(true);
        ratePlan.setIsRefundable(true);
    }

    @Test
    void testCreateRatePlan_Success() {
        // Given
        when(ratePlanRepository.findByCode("STANDARD")).thenReturn(Optional.empty());
        when(ratePlanRepository.save(any(RatePlan.class))).thenReturn(ratePlan);

        // When
        RatePlan result = rateManagementService.createRatePlan(ratePlanDto, "admin");

        // Then
        assertNotNull(result);
        assertEquals("STANDARD", result.getCode());
        assertEquals("Standard Rate", result.getName());
        verify(ratePlanRepository).findByCode("STANDARD");
        verify(ratePlanRepository).save(any(RatePlan.class));
    }

    @Test
    void testCreateRatePlan_DuplicateCode() {
        // Given
        when(ratePlanRepository.findByCode("STANDARD")).thenReturn(Optional.of(ratePlan));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> rateManagementService.createRatePlan(ratePlanDto, "admin"));
        
        assertEquals("Rate plan with code STANDARD already exists", exception.getMessage());
        verify(ratePlanRepository).findByCode("STANDARD");
        verify(ratePlanRepository, never()).save(any(RatePlan.class));
    }

    @Test
    void testCalculateRate_Success() {
        // Given
        RateCalculationRequest request = new RateCalculationRequest();
        request.setPropertyId("PROP001");
        request.setRoomType("DELUXE");
        request.setCheckInDate(LocalDate.now());
        request.setCheckOutDate(LocalDate.now().plusDays(2));
        request.setNumberOfGuests(2);
        request.setRatePlanCode("STANDARD");

        when(ratePlanRepository.findByCode("STANDARD")).thenReturn(Optional.of(ratePlan));
        when(seasonalRateRepository.findApplicableSeasonalRates(eq(1L), any(LocalDate.class)))
            .thenReturn(Arrays.asList());
        when(rateRestrictionRepository.findByRatePlanIdAndRestrictionDate(eq(1L), any(LocalDate.class)))
            .thenReturn(Optional.empty());

        // When
        RateCalculationResponse response = rateManagementService.calculateRate(request);

        // Then
        assertNotNull(response);
        assertEquals("STANDARD", response.getRatePlanCode());
        assertEquals("Standard Rate", response.getRatePlanName());
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(2, response.getNumberOfNights());
        assertEquals(new BigDecimal("100.00"), response.getAverageNightlyRate());
        assertTrue(response.getIsRefundable());
    }

    @Test
    void testGetActiveRatePlansForProperty() {
        // Given
        when(ratePlanRepository.findByPropertyIdAndIsActiveTrue("PROP001"))
            .thenReturn(Arrays.asList(ratePlan));

        // When
        var result = rateManagementService.getActiveRatePlansForProperty("PROP001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("STANDARD", result.get(0).getCode());
    }
}