package net.navinkumar.offersystem.repository;

import net.navinkumar.offersystem.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    Optional<Offer> findByCode(String code);
    
    List<Offer> findByPropertyIdAndIsActiveTrue(String propertyId);
    
    List<Offer> findByAuthorizedDepartmentAndIsActiveTrue(String department);
    
    @Query("SELECT o FROM Offer o WHERE o.propertyId = :propertyId " +
           "AND o.isActive = true " +
           "AND o.validFrom <= :currentDate " +
           "AND (o.validTo IS NULL OR o.validTo >= :currentDate)")
    List<Offer> findValidOffersForProperty(@Param("propertyId") String propertyId,
                                         @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT o FROM Offer o WHERE o.propertyId = :propertyId " +
           "AND o.authorizedDepartment = :department " +
           "AND o.isActive = true " +
           "AND o.validFrom <= :currentDate " +
           "AND (o.validTo IS NULL OR o.validTo >= :currentDate)")
    List<Offer> findValidOffersForDepartment(@Param("propertyId") String propertyId,
                                           @Param("department") String department,
                                           @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT o FROM Offer o WHERE o.isActive = true " +
           "AND o.validFrom <= :currentDate " +
           "AND (o.validTo IS NULL OR o.validTo >= :currentDate) " +
           "AND (o.usageLimit IS NULL OR o.usageCount < o.usageLimit)")
    List<Offer> findAvailableOffers(@Param("currentDate") LocalDate currentDate);
}