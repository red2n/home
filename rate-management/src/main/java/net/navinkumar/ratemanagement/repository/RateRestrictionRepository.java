package net.navinkumar.ratemanagement.repository;

import net.navinkumar.ratemanagement.entity.RateRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRestrictionRepository extends JpaRepository<RateRestriction, Long> {
    
    List<RateRestriction> findByRatePlanId(Long ratePlanId);
    
    Optional<RateRestriction> findByRatePlanIdAndRestrictionDate(Long ratePlanId, LocalDate restrictionDate);
    
    @Query("SELECT rr FROM RateRestriction rr WHERE rr.ratePlan.id = :ratePlanId " +
           "AND rr.restrictionDate BETWEEN :startDate AND :endDate")
    List<RateRestriction> findByRatePlanIdAndDateRange(@Param("ratePlanId") Long ratePlanId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}