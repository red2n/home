package net.navinkumar.integrationservice.repository;

import net.navinkumar.integrationservice.model.Integration;
import net.navinkumar.integrationservice.model.IntegrationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Integration entity
 */
@Repository
public interface IntegrationRepository extends JpaRepository<Integration, Long> {
    
    Optional<Integration> findByIntegrationCode(String integrationCode);
    
    List<Integration> findByIntegrationType(IntegrationType integrationType);
    
    List<Integration> findByActiveTrue();
    
    List<Integration> findByIntegrationTypeAndActiveTrue(IntegrationType integrationType);
}