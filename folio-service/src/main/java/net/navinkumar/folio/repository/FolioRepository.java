package net.navinkumar.folio.repository;

import net.navinkumar.folio.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {
    
    Optional<Folio> findByFolioNumber(String folioNumber);
    
    List<Folio> findByGuestId(Long guestId);
    
    List<Folio> findByReservationId(Long reservationId);
    
    List<Folio> findByStatus(Folio.FolioStatus status);
    
    @Query("SELECT f FROM Folio f WHERE f.guestId = :guestId AND f.status = :status")
    List<Folio> findByGuestIdAndStatus(@Param("guestId") Long guestId, @Param("status") Folio.FolioStatus status);
}