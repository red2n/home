package net.navinkumar.offersystem.service;

import net.navinkumar.offersystem.dto.OfferDto;
import net.navinkumar.offersystem.dto.OfferEligibilityRequest;
import net.navinkumar.offersystem.dto.OfferEligibilityResponse;
import net.navinkumar.offersystem.entity.Offer;
import net.navinkumar.offersystem.repository.OfferRepository;
import net.navinkumar.offersystem.repository.OfferApprovalRepository;
import net.navinkumar.offersystem.repository.OfferApplicationRepository;
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
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferApprovalRepository offerApprovalRepository;

    @Mock
    private OfferApplicationRepository offerApplicationRepository;

    @InjectMocks
    private OfferService offerService;

    private OfferDto offerDto;
    private Offer offer;

    @BeforeEach
    void setUp() {
        offerDto = new OfferDto();
        offerDto.setCode("WEEKEND20");
        offerDto.setName("Weekend 20% Discount");
        offerDto.setDescription("20% discount for weekend stays");
        offerDto.setPropertyId("PROP001");
        offerDto.setOfferType(Offer.OfferType.ROOM_DISCOUNT);
        offerDto.setDiscountType(Offer.DiscountType.PERCENTAGE);
        offerDto.setDiscountValue(new BigDecimal("20.00"));
        offerDto.setValidFrom(LocalDate.now());
        offerDto.setValidTo(LocalDate.now().plusDays(30));
        offerDto.setIsActive(true);
        offerDto.setRequiresApproval(false);
        offerDto.setAuthorizedDepartment("SALES");
        offerDto.setMinimumStay(2);
        offerDto.setApplicableRoomTypes("DELUXE,SUITE");
        offerDto.setUsageLimit(100);

        offer = new Offer();
        offer.setId(1L);
        offer.setCode("WEEKEND20");
        offer.setName("Weekend 20% Discount");
        offer.setDescription("20% discount for weekend stays");
        offer.setPropertyId("PROP001");
        offer.setOfferType(Offer.OfferType.ROOM_DISCOUNT);
        offer.setDiscountType(Offer.DiscountType.PERCENTAGE);
        offer.setDiscountValue(new BigDecimal("20.00"));
        offer.setValidFrom(LocalDate.now());
        offer.setValidTo(LocalDate.now().plusDays(30));
        offer.setIsActive(true);
        offer.setRequiresApproval(false);
        offer.setAuthorizedDepartment("SALES");
        offer.setMinimumStay(2);
        offer.setApplicableRoomTypes("DELUXE,SUITE");
        offer.setUsageLimit(100);
        offer.setUsageCount(0);
    }

    @Test
    void testCreateOffer_Success() {
        // Given
        when(offerRepository.findByCode("WEEKEND20")).thenReturn(Optional.empty());
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // When
        Offer result = offerService.createOffer(offerDto, "admin");

        // Then
        assertNotNull(result);
        assertEquals("WEEKEND20", result.getCode());
        assertEquals("Weekend 20% Discount", result.getName());
        verify(offerRepository).findByCode("WEEKEND20");
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testCreateOffer_DuplicateCode() {
        // Given
        when(offerRepository.findByCode("WEEKEND20")).thenReturn(Optional.of(offer));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> offerService.createOffer(offerDto, "admin"));
        
        assertEquals("Offer with code WEEKEND20 already exists", exception.getMessage());
        verify(offerRepository).findByCode("WEEKEND20");
        verify(offerRepository, never()).save(any(Offer.class));
    }

    @Test
    void testCheckOfferEligibility_EligibleOffer() {
        // Given
        OfferEligibilityRequest request = new OfferEligibilityRequest();
        request.setPropertyId("PROP001");
        request.setRoomType("DELUXE");
        request.setRatePlanCode("STANDARD");
        request.setBaseAmount(new BigDecimal("200.00"));
        request.setStayDuration(2);
        request.setAdvanceBookingDays(5);
        request.setDepartment("SALES");

        when(offerRepository.findValidOffersForProperty("PROP001", LocalDate.now()))
            .thenReturn(Arrays.asList(offer));

        // When
        OfferEligibilityResponse response = offerService.checkOfferEligibility(request);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("200.00"), response.getOriginalAmount());
        assertEquals(1, response.getEligibleOffers().size());
        
        OfferEligibilityResponse.EligibleOffer eligibleOffer = response.getEligibleOffers().get(0);
        assertEquals("WEEKEND20", eligibleOffer.getOfferCode());
        assertEquals(new BigDecimal("40.00"), eligibleOffer.getDiscountAmount()); // 20% of 200
        assertEquals(new BigDecimal("160.00"), eligibleOffer.getFinalAmount());
        assertFalse(eligibleOffer.getRequiresApproval());
    }

    @Test
    void testCheckOfferEligibility_NotEligibleRoomType() {
        // Given
        OfferEligibilityRequest request = new OfferEligibilityRequest();
        request.setPropertyId("PROP001");
        request.setRoomType("STANDARD"); // Not eligible room type
        request.setRatePlanCode("STANDARD");
        request.setBaseAmount(new BigDecimal("200.00"));
        request.setStayDuration(2);
        request.setAdvanceBookingDays(5);
        request.setDepartment("SALES");

        when(offerRepository.findValidOffersForProperty("PROP001", LocalDate.now()))
            .thenReturn(Arrays.asList(offer));

        // When
        OfferEligibilityResponse response = offerService.checkOfferEligibility(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getEligibleOffers().size());
        assertEquals(new BigDecimal("200.00"), response.getOriginalAmount());
        assertEquals(BigDecimal.ZERO, response.getBestDiscountAmount());
    }

    @Test
    void testCheckOfferEligibility_InsufficientStayDuration() {
        // Given
        OfferEligibilityRequest request = new OfferEligibilityRequest();
        request.setPropertyId("PROP001");
        request.setRoomType("DELUXE");
        request.setRatePlanCode("STANDARD");
        request.setBaseAmount(new BigDecimal("200.00"));
        request.setStayDuration(1); // Less than minimum stay of 2
        request.setAdvanceBookingDays(5);
        request.setDepartment("SALES");

        when(offerRepository.findValidOffersForProperty("PROP001", LocalDate.now()))
            .thenReturn(Arrays.asList(offer));

        // When
        OfferEligibilityResponse response = offerService.checkOfferEligibility(request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getEligibleOffers().size());
    }

    @Test
    void testGetValidOffersForProperty() {
        // Given
        when(offerRepository.findValidOffersForProperty("PROP001", LocalDate.now()))
            .thenReturn(Arrays.asList(offer));

        // When
        var result = offerService.getValidOffersForProperty("PROP001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WEEKEND20", result.get(0).getCode());
    }

    @Test
    void testGetValidOffersForDepartment() {
        // Given
        when(offerRepository.findValidOffersForDepartment("PROP001", "SALES", LocalDate.now()))
            .thenReturn(Arrays.asList(offer));

        // When
        var result = offerService.getValidOffersForDepartment("PROP001", "SALES");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WEEKEND20", result.get(0).getCode());
        assertEquals("SALES", result.get(0).getAuthorizedDepartment());
    }
}