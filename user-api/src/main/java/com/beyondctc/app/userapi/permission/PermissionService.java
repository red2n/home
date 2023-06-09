package com.beyondctc.app.userapi.permission;

import com.beyondctc.app.userapi.model.PermissionLookup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    public List<PermissionLookup> getPermission() {
        return repository.findAll();
    }
}
