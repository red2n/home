package net.navinkumar.integrationservice.controller;

import net.navinkumar.integrationservice.model.Integration;
import net.navinkumar.integrationservice.model.IntegrationType;
import net.navinkumar.integrationservice.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller for Integration Management operations
 */
@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {
    
    private final IntegrationService integrationService;
    
    /**
     * Get all active integrations
     */
    @GetMapping
    public ResponseEntity<List<Integration>> getActiveIntegrations() {
        List<Integration> integrations = integrationService.getActiveIntegrations();
        return ResponseEntity.ok(integrations);
    }
    
    /**
     * Get integration by code
     */
    @GetMapping("/{integrationCode}")
    public ResponseEntity<Integration> getIntegrationByCode(@PathVariable String integrationCode) {
        return integrationService.getIntegrationByCode(integrationCode)
            .map(integration -> ResponseEntity.ok(integration))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get integrations by type
     */
    @GetMapping("/type/{integrationType}")
    public ResponseEntity<List<Integration>> getIntegrationsByType(@PathVariable IntegrationType integrationType) {
        List<Integration> integrations = integrationService.getIntegrationsByType(integrationType);
        return ResponseEntity.ok(integrations);
    }
    
    /**
     * Create new integration
     */
    @PostMapping
    public ResponseEntity<Integration> createIntegration(@RequestBody Integration integration) {
        Integration createdIntegration = integrationService.createIntegration(integration);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIntegration);
    }
    
    /**
     * Update existing integration
     */
    @PutMapping("/{integrationId}")
    public ResponseEntity<Integration> updateIntegration(@PathVariable Long integrationId, @RequestBody Integration integration) {
        try {
            Integration updatedIntegration = integrationService.updateIntegration(integrationId, integration);
            return ResponseEntity.ok(updatedIntegration);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Test integration connectivity
     */
    @PostMapping("/{integrationCode}/test")
    public Mono<ResponseEntity<Boolean>> testIntegration(@PathVariable String integrationCode) {
        return integrationService.testIntegration(integrationCode)
            .map(success -> ResponseEntity.ok(success))
            .onErrorReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(false));
    }
    
    /**
     * Toggle integration status (activate/deactivate)
     */
    @PatchMapping("/{integrationId}/status")
    public ResponseEntity<Integration> toggleIntegrationStatus(@PathVariable Long integrationId, @RequestParam boolean active) {
        try {
            Integration updatedIntegration = integrationService.toggleIntegrationStatus(integrationId, active);
            return ResponseEntity.ok(updatedIntegration);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}