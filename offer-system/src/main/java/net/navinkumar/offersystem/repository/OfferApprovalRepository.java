package net.navinkumar.offersystem.repository;

import net.navinkumar.offersystem.entity.OfferApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferApprovalRepository extends JpaRepository<OfferApproval, Long> {
    
    List<OfferApproval> findByOfferIdAndStatus(Long offerId, OfferApproval.ApprovalStatus status);
    
    Optional<OfferApproval> findByReservationIdAndStatus(String reservationId, OfferApproval.ApprovalStatus status);
    
    List<OfferApproval> findByRequestedByOrderByRequestedAtDesc(String requestedBy);
    
    @Query("SELECT oa FROM OfferApproval oa JOIN oa.offer o " +
           "WHERE o.authorizedDepartment = :department " +
           "AND oa.status = :status " +
           "ORDER BY oa.requestedAt ASC")
    List<OfferApproval> findPendingApprovalsByDepartment(@Param("department") String department,
                                                        @Param("status") OfferApproval.ApprovalStatus status);
    
    List<OfferApproval> findByApprovedByOrderByApprovedAtDesc(String approvedBy);
}