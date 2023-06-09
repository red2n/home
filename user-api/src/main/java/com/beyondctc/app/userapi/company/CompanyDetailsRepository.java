package com.beyondctc.app.userapi.company;

import com.beyondctc.app.userapi.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDetailsRepository  extends JpaRepository<CompanyProfile, Long> {
}
