package com.beyondctc.app.userapi.company;

import com.beyondctc.app.userapi.model.CompanyProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyProfile>> getCompanies(){
        return  new ResponseEntity<>(companyService.getCompanies(), HttpStatus.OK);
    }
}
