package net.navinkumar.integrationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents an external system integration configuration
 */
@Entity
@Table(name = "integrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Integration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String integrationCode;
    
    @Column(nullable = false)
    private String integrationName;
    
    @Enumerated(EnumType.STRING)
    private IntegrationType integrationType;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    private String baseUrl;
    private String apiVersion;
    private String username;
    private String password;
    private String apiKey;
    private String secretKey;
    
    @Column(columnDefinition = "TEXT")
    private String configuration;
    
    @Column(columnDefinition = "TEXT")
    private String mappingConfiguration;
    
    private Integer timeoutSeconds = 30;
    private Integer retryAttempts = 3;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}