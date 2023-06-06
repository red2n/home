package com.beyondctc.app.userapi.access;

import com.beyondctc.app.userapi.model.AccessGroup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessService {

    private final AccessRepository accessRepository;

    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }


    public List<AccessGroup> getUserAccess() {
        return accessRepository.findAll();
    }

}
