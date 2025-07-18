package net.navinkumar.ratemanagement.repository;

import net.navinkumar.ratemanagement.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {
    
    Optional<RatePlan> findByCode(String code);
    
    List<RatePlan> findByPropertyIdAndIsActiveTrue(String propertyId);
    
    List<RatePlan> findByPropertyIdAndRoomTypeAndIsActiveTrue(String propertyId, String roomType);
    
    @Query("SELECT rp FROM RatePlan rp WHERE rp.propertyId = :propertyId " +
           "AND rp.roomType = :roomType AND rp.isActive = true " +
           "AND (rp.validFrom <= :checkDate AND (rp.validTo IS NULL OR rp.validTo >= :checkDate))")
    List<RatePlan> findValidRatePlansForDate(@Param("propertyId") String propertyId,
                                           @Param("roomType") String roomType,
                                           @Param("checkDate") LocalDate checkDate);
    
    @Query("SELECT rp FROM RatePlan rp WHERE rp.propertyId = :propertyId " +
           "AND rp.isActive = true " +
           "AND (rp.validFrom <= :checkDate AND (rp.validTo IS NULL OR rp.validTo >= :checkDate))")
    List<RatePlan> findValidRatePlansForPropertyAndDate(@Param("propertyId") String propertyId,
                                                       @Param("checkDate") LocalDate checkDate);
}