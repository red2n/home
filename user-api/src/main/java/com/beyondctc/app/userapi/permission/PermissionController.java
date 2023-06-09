package com.beyondctc.app.userapi.permission;

import com.beyondctc.app.userapi.model.PermissionLookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private final PermissionService service;


    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PermissionLookup>> getPermission() {
        return new ResponseEntity<>(service.getPermission(), HttpStatus.OK);
    }
}
