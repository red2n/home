package net.navinkumar.folio.service;

import net.navinkumar.folio.dto.FolioRequest;
import net.navinkumar.folio.entity.Folio;
import net.navinkumar.folio.repository.FolioRepository;
import net.navinkumar.folio.repository.FolioChargeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolioServiceTest {

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private FolioChargeRepository folioChargeRepository;

    @InjectMocks
    private FolioService folioService;

    @Test
    void createFolio_ShouldReturnCreatedFolio() {
        // Given
        FolioRequest request = new FolioRequest();
        request.setGuestId(1L);
        request.setReservationId(1L);
        request.setInitialAmount(new BigDecimal("100.00"));

        Folio savedFolio = new Folio();
        savedFolio.setId(1L);
        savedFolio.setGuestId(1L);
        savedFolio.setReservationId(1L);
        savedFolio.setStatus(Folio.FolioStatus.OPEN);

        when(folioRepository.save(any(Folio.class))).thenReturn(savedFolio);

        // When
        Folio result = folioService.createFolio(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getGuestId());
        assertEquals(1L, result.getReservationId());
        assertEquals(Folio.FolioStatus.OPEN, result.getStatus());
    }

    @Test
    void closeFolio_ShouldUpdateStatusToClosed() {
        // Given
        Folio folio = new Folio();
        folio.setId(1L);
        folio.setStatus(Folio.FolioStatus.OPEN);

        when(folioRepository.findById(1L)).thenReturn(java.util.Optional.of(folio));
        when(folioRepository.save(any(Folio.class))).thenReturn(folio);

        // When
        Folio result = folioService.closeFolio(1L);

        // Then
        assertEquals(Folio.FolioStatus.CLOSED, result.getStatus());
    }
}