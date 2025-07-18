package net.navinkumar.offersystem.repository;

import net.navinkumar.offersystem.entity.OfferApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferApplicationRepository extends JpaRepository<OfferApplication, Long> {
    
    List<OfferApplication> findByReservationId(String reservationId);
    
    List<OfferApplication> findByGuestId(String guestId);
    
    List<OfferApplication> findByOfferIdOrderByAppliedAtDesc(Long offerId);
    
    @Query("SELECT COUNT(oa) FROM OfferApplication oa WHERE oa.offer.id = :offerId")
    Long countApplicationsByOfferId(@Param("offerId") Long offerId);
    
    @Query("SELECT oa FROM OfferApplication oa WHERE oa.appliedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY oa.appliedAt DESC")
    List<OfferApplication> findApplicationsInDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
}