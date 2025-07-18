package net.navinkumar.ratemanagement.repository;

import net.navinkumar.ratemanagement.entity.SeasonalRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeasonalRateRepository extends JpaRepository<SeasonalRate, Long> {
    
    List<SeasonalRate> findByRatePlanIdAndIsActiveTrue(Long ratePlanId);
    
    @Query("SELECT sr FROM SeasonalRate sr WHERE sr.ratePlan.id = :ratePlanId " +
           "AND sr.isActive = true " +
           "AND sr.startDate <= :checkDate AND sr.endDate >= :checkDate " +
           "ORDER BY sr.priorityLevel DESC")
    List<SeasonalRate> findApplicableSeasonalRates(@Param("ratePlanId") Long ratePlanId,
                                                  @Param("checkDate") LocalDate checkDate);
}