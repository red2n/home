package net.navinkumar.integrationservice.service;

import net.navinkumar.integrationservice.model.Integration;
import net.navinkumar.integrationservice.model.IntegrationType;
import net.navinkumar.integrationservice.repository.IntegrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing external system integrations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {
    
    private final IntegrationRepository integrationRepository;
    private final WebClient.Builder webClientBuilder;
    
    /**
     * Get all active integrations
     */
    public List<Integration> getActiveIntegrations() {
        return integrationRepository.findByActiveTrue();
    }
    
    /**
     * Get integration by code
     */
    public Optional<Integration> getIntegrationByCode(String integrationCode) {
        return integrationRepository.findByIntegrationCode(integrationCode);
    }
    
    /**
     * Get integrations by type
     */
    public List<Integration> getIntegrationsByType(IntegrationType integrationType) {
        return integrationRepository.findByIntegrationTypeAndActiveTrue(integrationType);
    }
    
    /**
     * Create new integration
     */
    public Integration createIntegration(Integration integration) {
        log.info("Creating new integration: {}", integration.getIntegrationCode());
        return integrationRepository.save(integration);
    }
    
    /**
     * Update existing integration
     */
    public Integration updateIntegration(Long integrationId, Integration integrationUpdate) {
        return integrationRepository.findById(integrationId)
            .map(existingIntegration -> {
                existingIntegration.setIntegrationName(integrationUpdate.getIntegrationName());
                existingIntegration.setIntegrationType(integrationUpdate.getIntegrationType());
                existingIntegration.setActive(integrationUpdate.getActive());
                existingIntegration.setBaseUrl(integrationUpdate.getBaseUrl());
                existingIntegration.setApiVersion(integrationUpdate.getApiVersion());
                existingIntegration.setUsername(integrationUpdate.getUsername());
                existingIntegration.setPassword(integrationUpdate.getPassword());
                existingIntegration.setApiKey(integrationUpdate.getApiKey());
                existingIntegration.setSecretKey(integrationUpdate.getSecretKey());
                existingIntegration.setConfiguration(integrationUpdate.getConfiguration());
                existingIntegration.setMappingConfiguration(integrationUpdate.getMappingConfiguration());
                existingIntegration.setTimeoutSeconds(integrationUpdate.getTimeoutSeconds());
                existingIntegration.setRetryAttempts(integrationUpdate.getRetryAttempts());
                log.info("Updating integration: {}", existingIntegration.getIntegrationCode());
                return integrationRepository.save(existingIntegration);
            })
            .orElseThrow(() -> new RuntimeException("Integration not found with id: " + integrationId));
    }
    
    /**
     * Test integration connectivity
     */
    public Mono<Boolean> testIntegration(String integrationCode) {
        return getIntegrationByCode(integrationCode)
            .map(integration -> {
                WebClient webClient = webClientBuilder
                    .baseUrl(integration.getBaseUrl())
                    .build();
                
                return webClient.get()
                    .uri("/health")
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(integration.getTimeoutSeconds()))
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false);
            })
            .orElse(Mono.just(false));
    }
    
    /**
     * Toggle integration status
     */
    public Integration toggleIntegrationStatus(Long integrationId, boolean active) {
        return integrationRepository.findById(integrationId)
            .map(integration -> {
                integration.setActive(active);
                log.info("Integration {} status changed to: {}", integration.getIntegrationCode(), active);
                return integrationRepository.save(integration);
            })
            .orElseThrow(() -> new RuntimeException("Integration not found with id: " + integrationId));
    }
}