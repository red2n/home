package net.navinkumar.integrationservice.service;

import net.navinkumar.integrationservice.model.Integration;
import net.navinkumar.integrationservice.model.IntegrationType;
import net.navinkumar.integrationservice.repository.IntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegrationServiceTest {

    @Mock
    private IntegrationRepository integrationRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private IntegrationService integrationService;

    private Integration testIntegration;

    @BeforeEach
    void setUp() {
        testIntegration = new Integration();
        testIntegration.setId(1L);
        testIntegration.setIntegrationCode("STRIPE_PAYMENT");
        testIntegration.setIntegrationName("Stripe Payment Gateway");
        testIntegration.setIntegrationType(IntegrationType.PAYMENT_GATEWAY);
        testIntegration.setActive(true);
        testIntegration.setBaseUrl("https://api.stripe.com");
        testIntegration.setTimeoutSeconds(30);
        testIntegration.setRetryAttempts(3);
    }

    @Test
    void getActiveIntegrations_ShouldReturnActiveIntegrations() {
        // Given
        List<Integration> expectedIntegrations = Arrays.asList(testIntegration);
        when(integrationRepository.findByActiveTrue()).thenReturn(expectedIntegrations);

        // When
        List<Integration> actualIntegrations = integrationService.getActiveIntegrations();

        // Then
        assertEquals(expectedIntegrations, actualIntegrations);
        verify(integrationRepository).findByActiveTrue();
    }

    @Test
    void getIntegrationByCode_ShouldReturnIntegration_WhenExists() {
        // Given
        when(integrationRepository.findByIntegrationCode("STRIPE_PAYMENT")).thenReturn(Optional.of(testIntegration));

        // When
        Optional<Integration> result = integrationService.getIntegrationByCode("STRIPE_PAYMENT");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testIntegration, result.get());
        verify(integrationRepository).findByIntegrationCode("STRIPE_PAYMENT");
    }

    @Test
    void getIntegrationByCode_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(integrationRepository.findByIntegrationCode("NON_EXISTENT")).thenReturn(Optional.empty());

        // When
        Optional<Integration> result = integrationService.getIntegrationByCode("NON_EXISTENT");

        // Then
        assertFalse(result.isPresent());
        verify(integrationRepository).findByIntegrationCode("NON_EXISTENT");
    }

    @Test
    void createIntegration_ShouldSaveAndReturnIntegration() {
        // Given
        when(integrationRepository.save(any(Integration.class))).thenReturn(testIntegration);

        // When
        Integration result = integrationService.createIntegration(testIntegration);

        // Then
        assertEquals(testIntegration, result);
        verify(integrationRepository).save(testIntegration);
    }

    @Test
    void getIntegrationsByType_ShouldReturnIntegrationsByType() {
        // Given
        List<Integration> expectedIntegrations = Arrays.asList(testIntegration);
        when(integrationRepository.findByIntegrationTypeAndActiveTrue(IntegrationType.PAYMENT_GATEWAY))
            .thenReturn(expectedIntegrations);

        // When
        List<Integration> actualIntegrations = integrationService.getIntegrationsByType(IntegrationType.PAYMENT_GATEWAY);

        // Then
        assertEquals(expectedIntegrations, actualIntegrations);
        verify(integrationRepository).findByIntegrationTypeAndActiveTrue(IntegrationType.PAYMENT_GATEWAY);
    }
}