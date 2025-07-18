package net.navinkumar.ratemanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rate_restrictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateRestriction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", nullable = false)
    private RatePlan ratePlan;
    
    @Column(name = "restriction_date", nullable = false)
    private LocalDate restrictionDate;
    
    @Column(name = "is_closed_to_arrival")
    private Boolean isClosedToArrival = false;
    
    @Column(name = "is_closed_to_departure")
    private Boolean isClosedToDeparture = false;
    
    @Column(name = "minimum_stay_through")
    private Integer minimumStayThrough;
    
    @Column(name = "maximum_stay_through")
    private Integer maximumStayThrough;
    
    @Column(name = "stop_sell")
    private Boolean stopSell = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
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