package net.navinkumar.channelmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a booking channel (OTA, GDS, direct booking, etc.)
 */
@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String channelCode;
    
    @Column(nullable = false)
    private String channelName;
    
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    private String apiEndpoint;
    private String username;
    private String password;
    private String apiKey;
    
    @Column(columnDefinition = "TEXT")
    private String configuration;
    
    private Double commissionPercentage;
    
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