package com.beyondctc.app.userapi.details;

import com.beyondctc.app.userapi.model.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailService {


    private final DetailsRepository detailsRepository;

    public DetailService(DetailsRepository detailsRepository) {
        this.detailsRepository = detailsRepository;
    }

    public List<Users> getUsers() {
        return detailsRepository.findAll();
    }

}
