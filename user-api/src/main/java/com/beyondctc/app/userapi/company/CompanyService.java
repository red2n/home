package com.beyondctc.app.userapi.company;

import com.beyondctc.app.userapi.model.CompanyProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyDetailsRepository companyDetailsRepository;

    public CompanyService(CompanyDetailsRepository companyDetailsRepository) {
        this.companyDetailsRepository = companyDetailsRepository;
    }

    public List<CompanyProfile> getCompanies(){
        return  companyDetailsRepository.findAll();
    }
}
