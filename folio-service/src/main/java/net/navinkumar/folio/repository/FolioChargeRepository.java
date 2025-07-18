package net.navinkumar.folio.repository;

import net.navinkumar.folio.entity.FolioCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolioChargeRepository extends JpaRepository<FolioCharge, Long> {
    
    List<FolioCharge> findByFolioId(Long folioId);
    
    List<FolioCharge> findByChargeType(FolioCharge.ChargeType chargeType);
}